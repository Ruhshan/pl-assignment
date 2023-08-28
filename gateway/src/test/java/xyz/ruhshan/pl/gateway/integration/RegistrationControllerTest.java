package xyz.ruhshan.pl.gateway.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import xyz.ruhshan.pl.common.dto.BatteryRegistrationRequest;
import xyz.ruhshan.pl.common.repository.UserRepository;
import xyz.ruhshan.pl.gateway.request.SignInRequest;
import xyz.ruhshan.pl.gateway.request.SignUpRequest;
import xyz.ruhshan.pl.gateway.response.SignInResponse;
import xyz.ruhshan.pl.gateway.service.AuthServiceImpl;

@SpringBootTest
@ActiveProfiles("dev")
@Testcontainers
@AutoConfigureMockMvc
public class RegistrationControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
        "postgres:14.1-alpine")
        .withDatabaseName("pl-integration-tests-db")
        .withUsername("sa")
        .withPassword("sa");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer(
        "rabbitmq:management");

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    AuthServiceImpl authService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    private String jwtToken = "";

    @BeforeEach
    public void acquireAuthToken() {
        String email = "abc@def.com";
        String password = "test1234";

        SignUpRequest signUpRequest = SignUpRequest.builder().email(email).password(password).build();
        authService.signUp(signUpRequest);

        SignInRequest signInRequest = new SignInRequest(email, password);

        SignInResponse signInResponse = authService.signIn(signInRequest);
        jwtToken = signInResponse.getToken();

    }

    @AfterEach
    public void deleteExistingUsers() {
        userRepository.deleteAll();
    }

    @Test
    public void shouldReturn403IfAuthTokenNotProvided() throws Exception {
        List<BatteryRegistrationRequest> batteryRegistrationRequestList = Instancio
            .ofList(BatteryRegistrationRequest.class).size(10).create();

        this.mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/battery-registration")
                .content(objectMapper.writeValueAsString(batteryRegistrationRequestList))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturn200IfValidAuthTokenProvided() throws Exception{

        List<BatteryRegistrationRequest> batteryRegistrationRequestList = Instancio
            .ofList(BatteryRegistrationRequest.class).size(10).create();

        this.mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/battery-registration")
                .content(objectMapper.writeValueAsString(batteryRegistrationRequestList))
                .header("Authorization", "Bearer "+jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    public void shouldPublishRegistrationRequestsToQueue() throws Exception {
        List<BatteryRegistrationRequest> batteryRegistrationRequestList = Instancio
            .ofList(BatteryRegistrationRequest.class).size(10).create();

        this.mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/battery-registration")
                .content(objectMapper.writeValueAsString(batteryRegistrationRequestList))
                .header("Authorization", "Bearer "+jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        List<BatteryRegistrationRequest> batteryRegistrationRequestsFromQueue= retrieveMessagesFrmQueue(10);


        Assertions.assertArrayEquals(batteryRegistrationRequestList.toArray(), batteryRegistrationRequestsFromQueue.toArray());


    }

    private List<BatteryRegistrationRequest> retrieveMessagesFrmQueue(int numberOfMessages) {
        List<BatteryRegistrationRequest> batteryRegistrationRequestsFromQueue = new ArrayList<>();

        while(batteryRegistrationRequestsFromQueue.size()!=numberOfMessages) {
            BatteryRegistrationRequest message = rabbitTemplate.receiveAndConvert("q.battery-registration",
                ParameterizedTypeReference.forType(BatteryRegistrationRequest.class));
            if(message!=null){
                batteryRegistrationRequestsFromQueue.add(message);
            }
        }

        return batteryRegistrationRequestsFromQueue;
    }
}