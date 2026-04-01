package madp.appdeployment.domain.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AppDeploymentSummaryResponseDto(
        @JsonProperty("project_id")
        Long projectId,

        @JsonProperty("running")
        int running,

        @JsonProperty("warning")
        int warning,

        @JsonProperty("state")
        String state
) {}
