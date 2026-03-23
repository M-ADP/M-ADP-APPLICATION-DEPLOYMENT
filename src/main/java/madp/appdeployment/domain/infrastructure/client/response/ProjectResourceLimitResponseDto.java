package madp.appdeployment.domain.infrastructure.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProjectResourceLimitResponseDto(
        @JsonProperty("project_id")
        Long projectId,

        @JsonProperty("max_cpu")
        Double maxCpu,

        @JsonProperty("max_memory")
        Double maxMemory,

        @JsonProperty("max_disk")
        Double maxDisk
) {
}
