package xyz.ruhshan.pl.gateway.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import xyz.ruhshan.pl.common.dto.RealtimeAggregatedStatDto;
import xyz.ruhshan.pl.common.dto.StatisticsResponseDto;
import xyz.ruhshan.pl.gateway.service.StatisticsService;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class StatisticsController extends ApiV1Controller{
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/battery-statistics-by-postcode/{postCodeStart}/{postCodeEnd}")
    public StatisticsResponseDto getBatteryStatisticsByPostCodeRange(@PathVariable Integer postCodeStart, @PathVariable
        Integer postCodeEnd) {
        return statisticsService.getStatisticsByPostCodeRange(postCodeStart, postCodeEnd);
    }

    @GetMapping("/battery-statistics-aggregated")
    public RealtimeAggregatedStatDto getAggregatedBatteryStatistics(){
        return statisticsService.getAggregatedStatistics();
    }

}
