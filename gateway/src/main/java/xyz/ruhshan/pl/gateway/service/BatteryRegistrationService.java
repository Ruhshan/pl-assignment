package xyz.ruhshan.pl.gateway.service;

import java.util.List;
import xyz.ruhshan.pl.common.dto.BatteryRegistrationRequest;

public interface BatteryRegistrationService {
    void registerBatteries(List<BatteryRegistrationRequest> batteryRegistrationRequestList);
}
