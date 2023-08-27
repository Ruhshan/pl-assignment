package xyz.ruhshan.pl.registration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.ruhshan.pl.common.dto.BatteryRegistrationRequest;

@Service
@Slf4j
public class RegistrationEventHandlerServiceImpl implements RegistrationEventHandlerService {
    @Override
    @RabbitListener(queues = {"q.battery-registration"})
    public void register(BatteryRegistrationRequest batteryRegistrationRequest) {
        log.info("Persisting {} in db", batteryRegistrationRequest);
    }
}
