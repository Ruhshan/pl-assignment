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


}
