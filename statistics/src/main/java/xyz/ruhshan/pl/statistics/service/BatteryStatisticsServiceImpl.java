package xyz.ruhshan.pl.statistics.service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
}
