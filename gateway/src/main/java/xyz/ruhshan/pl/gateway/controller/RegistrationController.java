package xyz.ruhshan.pl.gateway.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import xyz.ruhshan.pl.common.dto.BatteryRegistrationRequest;
import xyz.ruhshan.pl.gateway.service.BatteryRegistrationService;

@RestController
@Validated
@Slf4j
@SecurityRequirement(name = "Bearer Authentication")
public class RegistrationController extends ApiV1Controller{
    private final BatteryRegistrationService batteryRegistrationService;

    public RegistrationController(BatteryRegistrationService batteryRegistrationService) {
        this.batteryRegistrationService = batteryRegistrationService;
    }

    @PostMapping(value = "/battery-registration", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void registerBattery(@RequestBody List<@Valid BatteryRegistrationRequest> batteryRegistrationRequestList) {
        log.info("Received registration request for {} batteries", batteryRegistrationRequestList.size());
        batteryRegistrationService.registerBatteries(batteryRegistrationRequestList);
    }

}
