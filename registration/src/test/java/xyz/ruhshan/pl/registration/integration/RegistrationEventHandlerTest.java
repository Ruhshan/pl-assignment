package xyz.ruhshan.pl.registration.integration;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import xyz.ruhshan.pl.common.dto.BatteryRegistrationRequest;
import xyz.ruhshan.pl.common.entity.Battery;
import xyz.ruhshan.pl.common.repository.BatteryRepository;
import xyz.ruhshan.pl.registration.service.RegistrationEventHandlerServiceImpl;

@SpringBootTest
@ActiveProfiles("dev")
@Testcontainers
public class RegistrationEventHandlerTest {
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
    BatteryRepository batteryRepository;

    @Autowired
    RegistrationEventHandlerServiceImpl registrationEventHandlerService;


    @AfterEach
    @BeforeEach
    public void clearBatteryDataFromDb(){
        batteryRepository.deleteAll();
    }

    @Test
    public void batteryRegistrationRequestShouldPersistInDb() {
        BatteryRegistrationRequest batteryRegistrationRequest = new BatteryRegistrationRequest("Cannington",
            6107, 13500.0);

        registrationEventHandlerService.register(batteryRegistrationRequest);

        Optional<Battery> batteryOpt = batteryRepository
            .findByPostCodeAndName(batteryRegistrationRequest.getPostcode(), batteryRegistrationRequest.getName());

        Assertions.assertTrue(batteryOpt.isPresent());

    }

    @Test
    public void persistedBatteryAttributesShouldMatchWithRequest() {

        BatteryRegistrationRequest batteryRegistrationRequest = new BatteryRegistrationRequest("Cannington",
            6107, 13500.0);

        registrationEventHandlerService.register(batteryRegistrationRequest);

        Optional<Battery> batteryOpt = batteryRepository
            .findByPostCodeAndName(batteryRegistrationRequest.getPostcode(), batteryRegistrationRequest.getName());

        Battery battery = batteryOpt.get();

        Assertions.assertEquals(batteryRegistrationRequest.getPostcode(), battery.getPostCode());
        Assertions.assertEquals(batteryRegistrationRequest.getName(), battery.getName());
        Assertions.assertEquals(batteryRegistrationRequest.getCapacity(), battery.getCapacity());


    }

    @Test
    public void registeringMultipleBatteryWithSameNameAndPostcodeShouldTreadAsOne() {
        List<BatteryRegistrationRequest> batteryRegistrationRequests = List.of(
            new BatteryRegistrationRequest("Cannington",
                6107, 13500.0),
            new BatteryRegistrationRequest("Cannington",
                6107, 13501.0),
            new BatteryRegistrationRequest("Cannington",
                6107, 13503.0)
        );

        batteryRegistrationRequests.forEach(batteryRegistrationRequest ->
            registrationEventHandlerService.register(batteryRegistrationRequest));

        List<Battery> allBatteries = batteryRepository.findAll();

        Assertions.assertEquals(1, allBatteries.size());
    }


}
