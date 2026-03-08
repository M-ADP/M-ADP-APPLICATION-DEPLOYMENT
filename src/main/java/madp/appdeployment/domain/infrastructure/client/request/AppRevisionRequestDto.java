package madp.appdeployment.domain.infrastructure.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AppRevisionRequestDto(
        @JsonProperty("application_id")
        @NotNull(message = "application_id는 존재해야 합니다.")
        @NotBlank(message = "application_id는 존재해야 합니다.")
        String applicationId,

        @JsonProperty("max_cpu")
        @NotNull(message = "max_cpu는 존재해야 합니다.")
        @DecimalMax(value = "1.0", message = "max_cpu는 1 이상이어야 합니다.")
        Double maxCpu,

        @JsonProperty("max_memory")
        @NotNull(message = "max_memory는 존재해야 합니다.")
        @Min(value = 1, message = "max_memory는 1 이상이어야 합니다.")
        Integer maxMemory,

        @JsonProperty("max_disk")
        @NotNull(message = "max_disk는 존재해야 합니다.")
        @Min(value = 1, message = "max_disk는 1 이상이어야 합니다.")
        Integer maxDisk
) {
}
