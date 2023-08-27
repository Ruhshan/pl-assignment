package xyz.ruhshan.pl.registration;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import xyz.ruhshan.pl.common.dto.BatteryRegistrationRequest;
import xyz.ruhshan.pl.common.entity.Battery;
import xyz.ruhshan.pl.common.repository.BatteryRepository;

@Service
@Slf4j
public class RegistrationEventHandlerServiceImpl implements RegistrationEventHandlerService {
    private final BatteryRepository batteryRepository;

    public RegistrationEventHandlerServiceImpl(BatteryRepository batteryRepository) {
        this.batteryRepository = batteryRepository;
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

        } else{
            log.info("Creating new battery record");

            Battery battery = Battery.builder()
                .name(batteryRegistrationRequest.getName())
                .postCode(batteryRegistrationRequest.getPostcode())
                .capacity(batteryRegistrationRequest.getCapacity())
                .build();

            batteryRepository.save(battery);

        }

    }
}
