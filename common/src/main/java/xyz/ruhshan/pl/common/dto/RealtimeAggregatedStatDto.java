package xyz.ruhshan.pl.common.dto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealtimeAggregatedStatDto {
    private Double totalCapacity;
    private Integer totalNumberOfBatteries;
    private Double averageCapacity;
    private Double minimumCapacity;
    private Double maximumCapacity;
    private List<Double> quartiles;

}
