package xyz.ruhshan.pl.statistics.service;

import xyz.ruhshan.pl.common.dto.StatisticsResponseDto;

public interface BatteryStatisticsService {
    StatisticsResponseDto getStatisticsByPostCodeRange(Integer postCodeStart, Integer postCodeEnd);
}
