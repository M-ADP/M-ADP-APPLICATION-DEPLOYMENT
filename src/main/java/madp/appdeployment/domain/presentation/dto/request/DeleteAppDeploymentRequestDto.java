package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteAppDeploymentRequestDto(
        @JsonProperty("application_id")
        Long appDeploymentId
) { }
