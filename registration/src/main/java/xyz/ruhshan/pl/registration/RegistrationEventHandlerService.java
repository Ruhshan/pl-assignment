package xyz.ruhshan.pl.registration;

import xyz.ruhshan.pl.common.dto.BatteryRegistrationRequest;

public interface RegistrationEventHandlerService {
    void register(BatteryRegistrationRequest batteryRegistrationRequest);

}
