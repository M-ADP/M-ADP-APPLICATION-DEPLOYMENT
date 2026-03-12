package madp.appdeployment.domain.infrastructure.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AppRevisionResponseDto(
        @JsonProperty("application_id")
        String applicationId,

        @JsonProperty("max_cpu")
        String maxCpu,

        @JsonProperty("max_memory")
        String maxMemory,

        @JsonProperty("max_disk")
        String maxDisk,

        @JsonProperty("revised")
        boolean revised
) {
}
