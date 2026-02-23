package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
        @Min(value = 1, message = "CPU는 최소 1개 이상이어야 합니다.")
        @Max(value = 4, message = "CPU는 최대 4개 이하이어야 합니다.")
        Integer cpu,

        @JsonProperty("memory")
        @NotNull(message = "메모리는 존재해야합니다.")
        @Min(value = 512, message = "메모리는 최소 512MB 이상이어야 합니다.")
        @Max(value = 2048, message = "메모리는 최대 2048MB 이하이어야 합니다.")
        Integer memory,

        @JsonProperty("disk")
        @NotNull(message = "디스크 크기는 존재해야합니다.")
        @Min(value = 2, message = "디스크 크기는 최소 2GB 이상이어야 합니다.")
        @Max(value = 16, message = "디스크 크기는 최대 16GB 이하이어야 합니다.")
        Integer disk,

        @JsonProperty("project_id")
        @NotNull(message = "프로젝트 ID는 존재해야 합니다.")
        @NotBlank(message = "프로젝트 ID는 존재해야 합니다.")
        String projectId,

        @JsonProperty("port")
        @NotNull(message = "포트는 존재해야 합니다.")
        Integer port
) {}
