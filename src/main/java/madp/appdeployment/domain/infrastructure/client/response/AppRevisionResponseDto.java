package madp.appdeployment.domain.infrastructure.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AppRevisionResponseDto(
        @JsonProperty("application_id")
        String applicationId,

        @JsonProperty("max_cpu")
        Integer maxCpu,

        @JsonProperty("max_memory")
        Double maxMemory,

        @JsonProperty("max_disk")
        Integer maxDisk,

        @JsonProperty("revised")
        boolean revised
) {
}
