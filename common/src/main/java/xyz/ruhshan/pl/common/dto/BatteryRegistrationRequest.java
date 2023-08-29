package xyz.ruhshan.pl.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatteryRegistrationRequest {
    @NotNull
    @Size(min = 5, max = 50)
    private String name;
    @NotNull
    @Min(10)
    @Max(10000)
    private Integer postcode;
    @NotNull
    @Min(0)
    @Max(100000)
    private Double capacity;

}
