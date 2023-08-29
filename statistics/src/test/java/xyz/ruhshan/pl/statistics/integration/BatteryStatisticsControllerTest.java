package xyz.ruhshan.pl.statistics.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import xyz.ruhshan.pl.common.dto.StatisticsResponseDto;
import xyz.ruhshan.pl.common.entity.Battery;
import xyz.ruhshan.pl.common.repository.BatteryRepository;

@SpringBootTest
@ActiveProfiles("dev")
@Testcontainers
@AutoConfigureMockMvc
public class BatteryStatisticsControllerTest {
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
    private MockMvc mockMvc;

    @Autowired
    private BatteryRepository batteryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();


    private List<Battery> BATTERY_DATA = List.of(
        new Battery("Cannington",6107,13500.0),
        new Battery("Midland",7057, 50500.0),
        new Battery("Armadale", 8000, 23500.0),
        new Battery("Lesmirdie",9076, 13500.0)
    );

    @BeforeEach
    public void populateDb(){
        batteryRepository.deleteAll();

        batteryRepository.saveAll(BATTERY_DATA);
    }


    @Test
    public void shouldProduceBadRequestErrorIfNoDataExists() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET,
            "/api/by-postcode-range/{start}/{end}",
            100, 200
            )).andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldProduce200IfDataExists() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET,
                "/api/by-postcode-range/{start}/{end}",
                6000, 8000
            )).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void shouldSanitizeInputUnderTheHood() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET,
                "/api/by-postcode-range/{start}/{end}",
                8000, 6000
            )).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatisticsResponseWithCorrectData() throws Exception {
        int postCodeStart = 6000;
        int postCodeEnd = 8000;

        Predicate<Battery> filterPredicate = battery -> battery.getPostCode()>=postCodeStart && battery.getPostCode()<=postCodeEnd;

        var sumOfBatteryCapacity = BATTERY_DATA.stream().filter(filterPredicate).mapToDouble(Battery::getCapacity).sum();
        var sortedNames = BATTERY_DATA.stream().filter(filterPredicate).map(Battery::getName).sorted().toList();

        StatisticsResponseDto expected = new StatisticsResponseDto(sortedNames, sumOfBatteryCapacity,
            sumOfBatteryCapacity/sortedNames.size());

        var content = this.mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET,
                "/api/by-postcode-range/{start}/{end}",
                8000, 6000
            )).andDo(print())
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        StatisticsResponseDto resultingResponse = objectMapper.readValue(content, StatisticsResponseDto.class);

        Assertions.assertEquals(expected, resultingResponse);




    }





}
