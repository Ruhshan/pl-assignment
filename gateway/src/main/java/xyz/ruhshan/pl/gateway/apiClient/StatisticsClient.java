package xyz.ruhshan.pl.gateway.apiClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import xyz.ruhshan.pl.common.dto.StatisticsResponseDto;

@FeignClient(name="statisticsClient", url="${statistics.url}")
public interface StatisticsClient {
    @RequestMapping(method = RequestMethod.GET, value = "/by-postcode-range/{start}/{end}")
    StatisticsResponseDto getStatisticsByPostCodeRange(@PathVariable Integer start, @PathVariable Integer end);

}
