package xyz.ruhshan.pl.statistics.service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import xyz.ruhshan.pl.common.dto.StatisticsResponseDto;
import xyz.ruhshan.pl.common.entity.Battery;
import xyz.ruhshan.pl.common.repository.BatteryRepository;

@Service
public class BatteryStatisticsServiceImpl implements BatteryStatisticsService {

    private final BatteryRepository batteryRepository;

    public BatteryStatisticsServiceImpl(BatteryRepository batteryRepository) {
        this.batteryRepository = batteryRepository;
    }


    @Override
    public StatisticsResponseDto getStatisticsByPostCodeRange(Integer postCodeStart, Integer postCodeEnd) {
        Double totalWatts = 0.0;
        List<String> names = new ArrayList<>();

        List<Battery> batteryList =  batteryRepository.findAllByPostCodeBetween(postCodeStart, postCodeEnd);

        for(Battery battery: batteryList){
            totalWatts+=battery.getCapacity();
            names.add(battery.getName());
        }

        Collections.sort(names);

        return new StatisticsResponseDto(names, totalWatts, totalWatts/names.size());
    }
}
