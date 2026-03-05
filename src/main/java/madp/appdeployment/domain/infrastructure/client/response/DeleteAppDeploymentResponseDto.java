package madp.appdeployment.domain.infrastructure.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteAppDeploymentResponseDto(
        @JsonProperty("application_id")
        String applicationId,

        @JsonProperty("name")
        String name,

        @JsonProperty("namespace")
        String namespace,

        @JsonProperty("deleted")
        boolean deleted
) {
}
