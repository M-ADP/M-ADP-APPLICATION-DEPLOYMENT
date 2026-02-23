package madp.appdeployment.domain.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateGithubInfoRequestDto(
        @NotNull(message = "app deployment id는 존재해야 합니다.")
        Long appDeploymentId,

        @NotNull(message = "owner는 존재해야 합니다.")
        @NotBlank(message = "owner는 존재해야 합니다.")
        String owner,

        @NotNull(message = "repository는 존재해야 합니다.")
        @NotBlank(message = "repository는 존재해야 합니다.")
        String repository,

        String branch
) {}
