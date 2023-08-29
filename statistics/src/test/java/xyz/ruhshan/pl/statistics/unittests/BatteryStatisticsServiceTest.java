package xyz.ruhshan.pl.statistics.unittests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import xyz.ruhshan.pl.common.dto.RealtimeAggregatedStatDto;
import xyz.ruhshan.pl.common.dto.StatisticsResponseDto;
import xyz.ruhshan.pl.common.entity.Battery;
import xyz.ruhshan.pl.common.repository.BatteryRepository;
import xyz.ruhshan.pl.statistics.service.BatteryStatisticsServiceImpl;

public class BatteryStatisticsServiceTest {
    @InjectMocks
    BatteryStatisticsServiceImpl batteryStatisticsService;

    @Mock
    BatteryRepository batteryRepository;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
    }

    private List<Battery> BATTERY_DATA = List.of(
        new Battery("Cannington",6107,13500.0),
        new Battery("Midland",6057, 50500.0),
        new Battery("Armadale", 6000, 23500.0),
        new Battery("Lesmirdie",6076, 13500.0)
    );

    @Test
    public void responseShouldContainSumOfAllBatteryCapacityReturnedFromRepository() {

        var sumOfBatteryCapacity = BATTERY_DATA.stream().mapToDouble(Battery::getCapacity).sum();

        when(batteryRepository.findAllByPostCodeBetween(any(), any())).thenReturn(BATTERY_DATA);

        StatisticsResponseDto statisticsResponseDto = batteryStatisticsService.getStatisticsByPostCodeRange(100, 200);

        Assertions.assertEquals(sumOfBatteryCapacity, statisticsResponseDto.getTotalWattCapacity());
    }

    @Test
    public void responseShouldContainNamesInAlphabeticallySortedList() {
        var sortedNames = BATTERY_DATA.stream().map(Battery::getName).sorted().toList();

        when(batteryRepository.findAllByPostCodeBetween(any(), any())).thenReturn(BATTERY_DATA);

        StatisticsResponseDto statisticsResponseDto = batteryStatisticsService.getStatisticsByPostCodeRange(100, 200);

        Assertions.assertArrayEquals(sortedNames.toArray(), statisticsResponseDto.getNames().toArray());
    }

    @Test
    public void responseShouldContainAverageWattCapacity() {
        var averageWatts = BATTERY_DATA.stream().mapToDouble(Battery::getCapacity).sum()/BATTERY_DATA.size();

        when(batteryRepository.findAllByPostCodeBetween(any(), any())).thenReturn(BATTERY_DATA);

        StatisticsResponseDto statisticsResponseDto = batteryStatisticsService.getStatisticsByPostCodeRange(100, 200);

        Assertions.assertEquals(averageWatts, statisticsResponseDto.getAverageWattCapacity());

    }

    @Test
    public void aggregatedStatShouldHaveTotalCapacityOfAllBatteries() {
        var totalCapacity = BATTERY_DATA.stream().mapToDouble(Battery::getCapacity).sum();
        when(batteryRepository.findAll()).thenReturn(BATTERY_DATA);

        RealtimeAggregatedStatDto aggregatedStatDto = batteryStatisticsService.getAggregatedStats();

        Assertions.assertEquals(totalCapacity, aggregatedStatDto.getTotalCapacity());

    }

    @Test
    public void aggregatedStatShouldHaveTotalNumberOfBatteries() {

        when(batteryRepository.findAll()).thenReturn(BATTERY_DATA);

        RealtimeAggregatedStatDto aggregatedStatDto = batteryStatisticsService.getAggregatedStats();

        Assertions.assertEquals(BATTERY_DATA.size(), aggregatedStatDto.getTotalNumberOfBatteries());

    }

    @Test
    public void aggregatedStatShouldHaveAverageCapacity() {
        var averageCapacity = BATTERY_DATA.stream().mapToDouble(Battery::getCapacity).sum()/BATTERY_DATA.size();

        when(batteryRepository.findAll()).thenReturn(BATTERY_DATA);

        RealtimeAggregatedStatDto aggregatedStatDto = batteryStatisticsService.getAggregatedStats();

        Assertions.assertEquals(averageCapacity, aggregatedStatDto.getAverageCapacity());

    }

    @Test
    public void aggregatedStatShouldMinimumCapacity() {
        var minCapacity = BATTERY_DATA.stream().mapToDouble(Battery::getCapacity).min();

        when(batteryRepository.findAll()).thenReturn(BATTERY_DATA);

        RealtimeAggregatedStatDto aggregatedStatDto = batteryStatisticsService.getAggregatedStats();

        Assertions.assertEquals(minCapacity.getAsDouble(), aggregatedStatDto.getMinimumCapacity());

    }

    @Test
    public void aggregatedStatShouldHaveMaximumCapacity() {
        var maxCapacity = BATTERY_DATA.stream().mapToDouble(Battery::getCapacity).max().getAsDouble();

        when(batteryRepository.findAll()).thenReturn(BATTERY_DATA);

        RealtimeAggregatedStatDto aggregatedStatDto = batteryStatisticsService.getAggregatedStats();

        Assertions.assertEquals(maxCapacity, aggregatedStatDto.getMaximumCapacity());

    }

    @Test
    public void aggregatedStatShouldHaveQuartiles() {
    List<Double> expectedQuartiles = List.of(13500.0, 13500.0, 18500.0);
        when(batteryRepository.findAll()).thenReturn(BATTERY_DATA);

        RealtimeAggregatedStatDto aggregatedStatDto = batteryStatisticsService.getAggregatedStats();

        Assertions.assertArrayEquals(expectedQuartiles.toArray(), aggregatedStatDto.getQuartiles().toArray());

    }


}
