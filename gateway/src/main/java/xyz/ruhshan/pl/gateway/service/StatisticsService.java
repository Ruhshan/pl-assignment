package xyz.ruhshan.pl.gateway.service;

import xyz.ruhshan.pl.common.dto.RealtimeAggregatedStatDto;
import xyz.ruhshan.pl.common.dto.StatisticsResponseDto;

public interface StatisticsService {
    StatisticsResponseDto getStatisticsByPostCodeRange(Integer postCodeStart, Integer postCodeEnd);
    RealtimeAggregatedStatDto getAggregatedStatistics();

}
