package madp.appdeployment.domain.infrastructure.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record NetworkMetricsResponseDto(
        @JsonProperty("start")
        String start,

        @JsonProperty("end")
        String end,

        @JsonProperty("rps")
        List<TimeSeriesDto> rps,

        @JsonProperty("by_response_code")
        Map<String, List<TimeSeriesDto>> byResponseCode,

        @JsonProperty("latency_p95")
        List<TimeSeriesDto> latencyP95
) {
    public record TimeSeriesDto(
            @JsonProperty("timestamp")
            String timestamp,

            @JsonProperty("value")
            Double value
    ) {}
}
