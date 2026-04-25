package madp.appdeployment.domain.infrastructure.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ResourceMetricsResponseDto(
        @JsonProperty("start")
        String start,

        @JsonProperty("end")
        String end,

        @JsonProperty("cpu")
        List<TimeSeriesDto> cpu,

        @JsonProperty("memory")
        List<TimeSeriesDto> memory,

        @JsonProperty("disk")
        List<TimeSeriesDto> disk
) {
    public record TimeSeriesDto(
            @JsonProperty("timestamp")
            String timestamp,

            @JsonProperty("value")
            Double value
    ) {}
}
