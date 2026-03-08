package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateResourceInfoRequestDto(
        @JsonProperty("application_id")
        @NotNull(message = "앱 배포 ID는 필수입니다.")
        Long appDeploymentId,

        @JsonProperty("max_cpu")
        @NotNull(message = "CPU 크기는 존재해야합니다.")
        @Min(value = 1, message = "CPU는 최소 1개 이상이어야 합니다.")
        @Max(value = 4, message = "CPU는 최대 4개 이하이어야 합니다.")
        Double cpu,

        @JsonProperty("max_memory")
        @NotNull(message = "메모리는 존재해야합니다.")
        @Min(value = 512, message = "메모리는 최소 512MB 이상이어야 합니다.")
        @Max(value = 2048, message = "메모리는 최대 2048MB 이하이어야 합니다.")
        Integer memory,

        @JsonProperty("max_disk")
        @NotNull(message = "디스크 크기는 존재해야합니다.")
        @Min(value = 2, message = "디스크 크기는 최소 2GB 이상이어야 합니다.")
        @Max(value = 16, message = "디스크 크기는 최대 16GB 이하이어야 합니다.")
        Integer disk
) {}