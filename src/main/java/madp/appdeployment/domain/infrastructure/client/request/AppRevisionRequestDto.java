package madp.appdeployment.domain.infrastructure.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AppRevisionRequestDto(
        @JsonProperty("application_id")
        @NotNull(message = "application_id는 존재해야 합니다.")
        @NotBlank(message = "application_id는 존재해야 합니다.")
        String applicationId,

        @JsonProperty("max_cpu")
        @NotNull(message = "max_cpu는 존재해야 합니다.")
        @NotBlank(message = "max_cpu는 존재해야 합니다.")
        @Pattern(regexp = "^[1-9]\\d*m$", message = "max_cpu는 m 단위(e.g. 500m)여야 합니다.")
        String maxCpu,

        @JsonProperty("max_memory")
        @NotNull(message = "max_memory는 존재해야 합니다.")
        @NotBlank(message = "max_memory는 존재해야 합니다.")
        @Pattern(regexp = "^[1-9]\\d*Mi$", message = "max_memory는 Mi 단위(e.g. 512Mi)여야 합니다.")
        String maxMemory,

        @JsonProperty("max_disk")
        @NotNull(message = "max_disk는 존재해야 합니다.")
        @NotBlank(message = "max_disk는 존재해야 합니다.")
        @Pattern(regexp = "^[1-9]\\d*Mi$", message = "max_disk는 Mi 단위(e.g. 2048Mi)여야 합니다.")
        String maxDisk
) {
}
