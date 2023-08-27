package xyz.ruhshan.pl.gateway.service;

import java.util.List;
import xyz.ruhshan.pl.gateway.request.BatteryRegistrationRequest;

public interface BatteryRegistrationService {
    void registerBatteries(List<BatteryRegistrationRequest> batteryRegistrationRequestList);
}
