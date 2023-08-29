package xyz.ruhshan.pl.gateway.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import xyz.ruhshan.pl.common.entity.User;
import xyz.ruhshan.pl.common.enums.Role;
import xyz.ruhshan.pl.common.repository.UserRepository;
import xyz.ruhshan.pl.gateway.request.SignInRequest;
import xyz.ruhshan.pl.gateway.request.SignUpRequest;
import xyz.ruhshan.pl.gateway.response.SignInResponse;
import xyz.ruhshan.pl.gateway.service.JwtService;

@SpringBootTest
@ActiveProfiles("dev")
@Testcontainers
@AutoConfigureMockMvc
public class AuthControllerTest {
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
    @Container
    @ServiceConnection
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis"))
        .withExposedPorts(6379);


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Value("${jwt.key}")
    private String jwtKey;

    @BeforeEach
    public void clearUserTable(){
        userRepository.deleteAll();
    }

    @Test
    public void shouldPersistUserInDbForValidSignUpRequest() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("abc@def.com");
        signUpRequest.setPassword("Test1234!");

        mockMvc.perform(MockMvcRequestBuilders.
            post("/api/v1/auth/signup")
            .content(objectMapper.writeValueAsString(signUpRequest))
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Assertions.assertTrue(userRepository.findByEmail(signUpRequest.getEmail()).isPresent());
    }

    @Test
    public void shouldProduceBadRequestIfUserAlreadyExists() throws Exception {
        User user = new User();
        user.setEmail("abc@def.com");
        user.setPassword(passwordEncoder.encode("Test1234!"));
        user.setRole(Role.USER);

        userRepository.save(user);

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("abc@def.com");
        signUpRequest.setPassword("Test1234!");

        mockMvc.perform(MockMvcRequestBuilders.
            post("/api/v1/auth/signup")
            .content(objectMapper.writeValueAsString(signUpRequest))
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());


    }

    @Test
    public void shouldProduceBadRequestIfPasswordIsTooSimple() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("abc@def.com");
        signUpRequest.setPassword("123123");

        mockMvc.perform(MockMvcRequestBuilders.
            post("/api/v1/auth/signup")
            .content(objectMapper.writeValueAsString(signUpRequest))
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldProduceBadRequestIfEmailIsNotValid() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("abc.com");
        signUpRequest.setPassword("Test1234!");

        mockMvc.perform(MockMvcRequestBuilders.
            post("/api/v1/auth/signup")
            .content(objectMapper.writeValueAsString(signUpRequest))
            .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnValidTokenForValidSignInRequest() throws Exception {
        User user = new User();
        user.setEmail("abc@def.com");
        user.setPassword(passwordEncoder.encode("Test1234!"));
        user.setRole(Role.USER);

        userRepository.save(user);

        SignInRequest signInRequest = new SignInRequest(user.getEmail(), "Test1234!");

        var responseString = mockMvc.perform(MockMvcRequestBuilders.
                post("/api/v1/auth/signin")
                .content(objectMapper.writeValueAsString(signInRequest))
                .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        SignInResponse responseObject = objectMapper.readValue(responseString, SignInResponse.class);


        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(jwtKey)).build();

        DecodedJWT decodedJWT = jwtVerifier.verify(responseObject.getToken());

        Assertions.assertEquals(user.getEmail(), decodedJWT.getSubject());


    }

    @Test
    public void shouldProduceForbiddenErrorIfSignInWithWrongCredentials() throws Exception{

        User user = new User();
        user.setEmail("abc@def.com");
        user.setPassword(passwordEncoder.encode("Test1234!"));
        user.setRole(Role.USER);

        userRepository.save(user);

        SignInRequest signInRequest = new SignInRequest(user.getEmail(), "1234!Test");

        mockMvc.perform(MockMvcRequestBuilders.
                post("/api/v1/auth/signin")
                .content(objectMapper.writeValueAsString(signInRequest))
                .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isForbidden());



    }



}
