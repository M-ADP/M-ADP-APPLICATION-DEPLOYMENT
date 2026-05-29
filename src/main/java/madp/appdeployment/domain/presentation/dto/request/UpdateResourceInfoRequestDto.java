package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record UpdateResourceInfoRequestDto(
        @JsonProperty("application_id")
        @NotNull(message = "앱 배포 ID는 필수입니다.")
        Long appDeploymentId,

        @JsonProperty("max_cpu")
        @NotNull(message = "CPU 크기는 존재해야합니다.")
        Double cpu,

        @JsonProperty("max_memory")
        @NotNull(message = "메모리는 존재해야합니다.")
        Double memory,

        @JsonProperty("max_disk")
        @NotNull(message = "디스크 크기는 존재해야합니다.")
        Integer disk
) {}
