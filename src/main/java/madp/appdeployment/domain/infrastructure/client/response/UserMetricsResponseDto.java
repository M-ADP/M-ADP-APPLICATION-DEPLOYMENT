package madp.appdeployment.domain.infrastructure.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserMetricsResponseDto(
        @JsonProperty("dau")
        Long dau,

        @JsonProperty("wau")
        Long wau,

        @JsonProperty("mau")
        Long mau
) {}
