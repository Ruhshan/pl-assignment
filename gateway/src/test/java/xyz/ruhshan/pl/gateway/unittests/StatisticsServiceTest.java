package xyz.ruhshan.pl.gateway.unittests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.lang.module.ResolutionException;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import xyz.ruhshan.pl.common.dto.RealtimeAggregatedStatDto;
import xyz.ruhshan.pl.common.dto.StatisticsResponseDto;
import xyz.ruhshan.pl.gateway.apiClient.StatisticsClient;
import xyz.ruhshan.pl.gateway.service.StatisticsServiceImpl;

public class StatisticsServiceTest {
    @InjectMocks
    StatisticsServiceImpl statisticsService;

    @Mock
    StatisticsClient statisticsClient;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnStatisticsResponseFromStatisticsClient(){
        StatisticsResponseDto statisticsResponseDtoFromClient = Instancio.create(StatisticsResponseDto.class);

        when(statisticsClient.getStatisticsByPostCodeRange(any(), any())).thenReturn(statisticsResponseDtoFromClient);

        StatisticsResponseDto statisticsResponseDtoFromService = statisticsService.getStatisticsByPostCodeRange(100,
            200);

        Assertions.assertEquals(statisticsResponseDtoFromClient, statisticsResponseDtoFromService);
    }

    @Test
    public void shouldRaiseResponseStatusExceptionIfErrorOccursDuringApiCall() {

        when(statisticsClient.getStatisticsByPostCodeRange(any(), any())).thenThrow(new RuntimeException("Something went wrong"));

        ResponseStatusException responseStatusException = Assertions.assertThrows(ResponseStatusException.class,
            ()->statisticsService.getStatisticsByPostCodeRange(100, 200));

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseStatusException.getStatusCode());
        Assertions.assertEquals("Something went wrong during fetching statistics", responseStatusException.getReason());
    }

    @Test
    public void shouldReturnAggregatedStatisticsReturnedFromClient() {
        RealtimeAggregatedStatDto aggregatedStatDto = Instancio.create(RealtimeAggregatedStatDto.class);

        when(statisticsClient.getAggregatedStat()).thenReturn(aggregatedStatDto);

        RealtimeAggregatedStatDto responseFromService = statisticsService.getAggregatedStatistics();

        Assertions.assertEquals(aggregatedStatDto, responseFromService);
    }



}
