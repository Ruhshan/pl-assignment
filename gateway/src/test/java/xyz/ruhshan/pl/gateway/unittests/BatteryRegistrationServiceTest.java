package xyz.ruhshan.pl.gateway.unittests;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import xyz.ruhshan.pl.common.dto.BatteryRegistrationRequest;
import xyz.ruhshan.pl.gateway.service.BatteryRegistrationServiceImpl;
import java.util.List;

public class BatteryRegistrationServiceTest {
    @InjectMocks
    BatteryRegistrationServiceImpl batteryRegistrationService;
    @Mock
    RabbitTemplate rabbitTemplate;

    @Captor
    ArgumentCaptor<BatteryRegistrationRequest> batteryRegistrationRequestArgumentCaptor;

    @Captor
    ArgumentCaptor<String> queueNameArgumentCaptor;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldPublishAllBatteryRegistrationRequestsToQueue() {
        List<BatteryRegistrationRequest> batteryRegistrationRequests = Instancio.ofList(BatteryRegistrationRequest.class).size(10).create();

        batteryRegistrationService.registerBatteries(batteryRegistrationRequests);

        verify(rabbitTemplate, times(10)).convertAndSend(queueNameArgumentCaptor.capture(),
            batteryRegistrationRequestArgumentCaptor.capture());

        List<BatteryRegistrationRequest> capturedRequests = batteryRegistrationRequestArgumentCaptor.getAllValues();

        Assertions.assertArrayEquals(batteryRegistrationRequests.toArray(), capturedRequests.toArray());
    }

}
