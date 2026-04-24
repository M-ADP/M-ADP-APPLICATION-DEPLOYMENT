package madp.appdeployment.domain.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateSecretResponseDto(
        @JsonProperty("namespace")
        String namespace,

        @JsonProperty("app_name")
        String appName,

        @JsonProperty("path")
        String path,

        @JsonProperty("policy_name")
        String policyName,

        @JsonProperty("role_name")
        String roleName
) {
}
