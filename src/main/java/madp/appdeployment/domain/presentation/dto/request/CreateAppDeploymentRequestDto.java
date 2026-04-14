package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// 값들은 정책에 따라 바뀔 예정.
// 추후, Properties를 통해, 외부에서 주입받도록 수정하겠음.
public record CreateAppDeploymentRequestDto(
        @JsonProperty("name")
        @NotNull(message = "이름은 존재해야합니다.")
        @NotBlank(message = "이름은 존재해야합니다.")
        String name,

        @JsonProperty("cpu")
        @NotNull(message = "CPU 크기는 존재해야합니다.")
        @DecimalMin(value = "0.1", message = "CPU는 최소 0.1 이상이어야 합니다.")
        @DecimalMax(value = "4.0", message = "CPU는 최대 4.0 이하이어야 합니다.")
        Double cpu,

        @JsonProperty("memory")
        @NotNull(message = "메모리는 존재해야합니다.")
        @DecimalMin(value = "0.25", message = "메모리는 최소 0.25GB 이상이어야 합니다.")
        @DecimalMax(value = "1.0", message = "메모리는 최대 1GB 이하이어야 합니다.")
        Double memory,

        @JsonProperty("disk")
        @NotNull(message = "디스크 크기는 존재해야합니다.")
        @Min(value = 2, message = "디스크 크기는 최소 2GB 이상이어야 합니다.")
        @Max(value = 50, message = "디스크 크기는 최대 50GB 이하이어야 합니다.")
        Integer disk,

        @JsonProperty("project_id")
        @NotNull(message = "프로젝트 ID는 존재해야 합니다.")
        @NotBlank(message = "프로젝트 ID는 존재해야 합니다.")
        String projectId
) {}
