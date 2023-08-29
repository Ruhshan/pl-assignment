package xyz.ruhshan.pl.statistics.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import xyz.ruhshan.pl.common.dto.RealtimeAggregatedStatDto;
import xyz.ruhshan.pl.common.dto.StatisticsResponseDto;
import xyz.ruhshan.pl.common.entity.Battery;
import xyz.ruhshan.pl.common.repository.BatteryRepository;

@Service
@Slf4j
public class BatteryStatisticsServiceImpl implements BatteryStatisticsService {

    private final BatteryRepository batteryRepository;


    public BatteryStatisticsServiceImpl(BatteryRepository batteryRepository) {
        this.batteryRepository = batteryRepository;

    }


    @Override
    public StatisticsResponseDto getStatisticsByPostCodeRange(Integer postCodeStart, Integer postCodeEnd) {

        if(postCodeEnd < postCodeStart) {
            var temp = postCodeEnd;
            postCodeEnd = postCodeStart;
            postCodeStart = temp;
        }


        log.info("Querying db for post code range {} to {}", postCodeStart, postCodeEnd);


        Double totalWatts = 0.0;
        List<String> names = new ArrayList<>();

        List<Battery> batteryList =  batteryRepository.findAllByPostCodeBetween(postCodeStart, postCodeEnd);

        if(batteryList.isEmpty()) {
            log.error("No battery exists for post code range {} to {}", postCodeStart, postCodeEnd);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No battery exists with given post code range");
        }

        for(Battery battery: batteryList){
            totalWatts+=battery.getCapacity();
            names.add(battery.getName());
        }

        Collections.sort(names);

        return new StatisticsResponseDto(names, totalWatts, totalWatts/names.size());
    }

    @Override
    @Cacheable("pl-cache")
    public RealtimeAggregatedStatDto getAggregatedStats() {
        List<Battery> batteryList = batteryRepository.findAll();

        var totalCapacity = batteryList.stream().mapToDouble(Battery::getCapacity).sum();
        var minCapacity = batteryList.stream().mapToDouble(Battery::getCapacity).min();
        var maxCapacity = batteryList.stream().mapToDouble(Battery::getCapacity).max();

        RealtimeAggregatedStatDto realtimeAggregatedStatDto = RealtimeAggregatedStatDto.builder()
            .totalNumberOfBatteries(batteryList.size())
            .totalCapacity(totalCapacity)
            .averageCapacity(totalCapacity/batteryList.size())
            .minimumCapacity(minCapacity.isPresent() ? minCapacity.getAsDouble() : null)
            .maximumCapacity(maxCapacity.isPresent() ? maxCapacity.getAsDouble(): null)
            .quartiles(calculateQuartiles(batteryList))
            .build();

        return realtimeAggregatedStatDto;
    }

    @Override
    @CacheEvict("pl-cache")
    @RabbitListener(queues = {"q.battery-registration-complete"})
    public void clearCachedAggregatedStat() {
        log.info("Clearing the cached stat");
    }

    private List<Double> calculateQuartiles(List<Battery> batteryList) {
        double[] sortedCapacity = batteryList.stream().mapToDouble(Battery::getCapacity).sorted().toArray();

        int n = sortedCapacity.length;
        int q1Index = n / 4;
        int q2Index = n / 2;
        int q3Index = 3 * n / 4;

        return List.of(calculateMedian(sortedCapacity, 0, q1Index),
            calculateMedian(sortedCapacity, 0, q2Index),
            calculateMedian(sortedCapacity, 0, q3Index));
    }

    private  double calculateMedian(double[] data, int startIndex, int endIndex) {
        int length = endIndex - startIndex + 1;
        int middleIndex = startIndex + length / 2;
        if (length % 2 == 0) {
            return (data[middleIndex - 1] + data[middleIndex]) / 2.0;
        } else {
            return data[middleIndex];
        }
    }
}
