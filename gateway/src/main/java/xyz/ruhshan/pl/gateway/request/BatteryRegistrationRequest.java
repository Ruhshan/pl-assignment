package xyz.ruhshan.pl.gateway.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatteryRegistrationRequest {
    @NotNull
    private String name;
    @NotNull
    private Integer postcode;
    @NotNull
    private Double capacity;

}
