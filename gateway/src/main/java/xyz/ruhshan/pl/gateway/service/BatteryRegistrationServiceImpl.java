package xyz.ruhshan.pl.gateway.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.ruhshan.pl.common.dto.BatteryRegistrationRequest;

@Service
@Slf4j
public class BatteryRegistrationServiceImpl implements BatteryRegistrationService {
    private final RabbitTemplate rabbitTemplate;
    private final String batteryRegistrationQueue;

    public BatteryRegistrationServiceImpl(RabbitTemplate rabbitTemplate,
        @Value("${queue.battery-registration}") String batteryRegistrationQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.batteryRegistrationQueue = batteryRegistrationQueue;
    }

    @Override
    public void registerBatteries(List<BatteryRegistrationRequest> batteryRegistrationRequestList) {
        batteryRegistrationRequestList.forEach(batteryRegistrationRequest ->{

            log.info("Publishing {} to queue", batteryRegistrationRequest);
            rabbitTemplate.convertAndSend(batteryRegistrationQueue, batteryRegistrationRequest);
        }
        );
    }
}
