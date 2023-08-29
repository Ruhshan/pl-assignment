package xyz.ruhshan.pl.registration.service;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.ruhshan.pl.common.dto.BatteryRegistrationRequest;
import xyz.ruhshan.pl.common.entity.Battery;
import xyz.ruhshan.pl.common.repository.BatteryRepository;

@Service
@Slf4j
public class RegistrationEventHandlerServiceImpl implements RegistrationEventHandlerService {
    private final BatteryRepository batteryRepository;
    private final RabbitTemplate rabbitTemplate;
    private final String batteryRegistrationCompleteQueue;

    public RegistrationEventHandlerServiceImpl(BatteryRepository batteryRepository, RabbitTemplate rabbitTemplate,
        @Value("${queue.battery-registration-complete}") String batteryRegistrationCompleteQueue) {
        this.batteryRepository = batteryRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.batteryRegistrationCompleteQueue = batteryRegistrationCompleteQueue;
    }

    @Override
    @RabbitListener(queues = {"q.battery-registration"})
    public void register(BatteryRegistrationRequest batteryRegistrationRequest) {
        log.info("Persisting {} in db", batteryRegistrationRequest);

        Optional<Battery> batteryOptional = batteryRepository.findByPostCodeAndName(batteryRegistrationRequest.getPostcode(),
            batteryRegistrationRequest.getName());

        if(batteryOptional.isPresent()) {
            Battery battery = batteryOptional.get();
            log.info("Updating existing battery id {} with capacity {}", battery.getId(), batteryRegistrationRequest.getCapacity());
            battery.setCapacity(batteryRegistrationRequest.getCapacity());

            batteryRepository.save(battery);

            rabbitTemplate.convertAndSend(batteryRegistrationCompleteQueue, battery);

        } else{
            log.info("Creating new battery record");

            Battery battery = Battery.builder()
                .name(batteryRegistrationRequest.getName())
                .postCode(batteryRegistrationRequest.getPostcode())
                .capacity(batteryRegistrationRequest.getCapacity())
                .build();

            batteryRepository.save(battery);

            rabbitTemplate.convertAndSend(batteryRegistrationCompleteQueue, battery);

        }

    }
}
