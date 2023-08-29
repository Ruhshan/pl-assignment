package xyz.ruhshan.pl.gateway.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import xyz.ruhshan.pl.common.dto.RealtimeAggregatedStatDto;
import xyz.ruhshan.pl.common.dto.StatisticsResponseDto;
import xyz.ruhshan.pl.gateway.apiClient.StatisticsClient;

@Service
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsClient statisticsClient;

    public StatisticsServiceImpl(StatisticsClient statisticsClient) {
        this.statisticsClient = statisticsClient;
    }

    @Override
    public StatisticsResponseDto getStatisticsByPostCodeRange(Integer postCodeStart, Integer postCodeEnd) {
        try{
            return statisticsClient.getStatisticsByPostCodeRange(postCodeStart, postCodeEnd);
        }catch (Exception e){
            log.error("Error occurred during statistics service call {}", e.getMessage());

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong during fetching statistics");
        }
    }

    @Override
    public RealtimeAggregatedStatDto getAggregatedStatistics() {
        try {
            return statisticsClient.getAggregatedStat();

        }catch (Exception e){
            log.error("Error occurred during statistics service call {}", e.getMessage());

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong during fetching statistics");
        }
    }
}
