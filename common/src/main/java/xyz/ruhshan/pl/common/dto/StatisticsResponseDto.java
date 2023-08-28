package xyz.ruhshan.pl.common.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsResponseDto {
    private List<String> names;
    private Double totalWattCapacity;
    @JsonProperty("averageWattCapacity")
    public Double getAverageWattCapacity() {
        return totalWattCapacity/names.size();
    }
}
