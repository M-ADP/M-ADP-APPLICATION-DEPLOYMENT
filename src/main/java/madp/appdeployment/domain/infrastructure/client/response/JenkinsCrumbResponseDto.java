package madp.appdeployment.domain.infrastructure.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JenkinsCrumbResponseDto(
        @JsonProperty("crumb")
        String crumb
) {}
