package xyz.ruhshan.pl.statistics.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import xyz.ruhshan.pl.common.dto.StatisticsResponseDto;
import xyz.ruhshan.pl.statistics.service.BatteryStatisticsService;

@RestController
public class BatteryStatisticsController {
    private final BatteryStatisticsService batteryStatisticsService;

    public BatteryStatisticsController(BatteryStatisticsService batteryStatisticsService) {
        this.batteryStatisticsService = batteryStatisticsService;
    }

    @GetMapping("/api/by-postcode-range/{start}/{end}")
    public StatisticsResponseDto getStatisticsByPostCodeRange(@PathVariable Integer start, @PathVariable Integer end){
        return batteryStatisticsService.getStatisticsByPostCodeRange(start, end);
    }
}
