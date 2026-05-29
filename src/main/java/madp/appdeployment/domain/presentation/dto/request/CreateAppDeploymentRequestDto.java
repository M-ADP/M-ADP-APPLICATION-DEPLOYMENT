package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAppDeploymentRequestDto(
        @JsonProperty("name")
        @NotNull(message = "이름은 존재해야합니다.")
        @NotBlank(message = "이름은 존재해야합니다.")
        String name,

        @JsonProperty("cpu")
        @NotNull(message = "CPU 크기는 존재해야합니다.")
        Double cpu,

        @JsonProperty("memory")
        @NotNull(message = "메모리는 존재해야합니다.")
        Double memory,

        @JsonProperty("disk")
        @NotNull(message = "디스크 크기는 존재해야합니다.")
        Integer disk,

        @JsonProperty("project_id")
        @NotNull(message = "프로젝트 ID는 존재해야 합니다.")
        @NotBlank(message = "프로젝트 ID는 존재해야 합니다.")
        String projectId
) {}
