package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateAppVersionRequestDto(
        @JsonProperty("app_deployment_id")
        @NotNull(message = "app deployment id는 존재해야합니다.")
        Long appDeploymentId,

        @JsonProperty("version")
        @NotNull(message = "version은 존재해야합니다.")
        @Positive(message = "version은 양수여야 합니다.")
        Integer version
) {}
