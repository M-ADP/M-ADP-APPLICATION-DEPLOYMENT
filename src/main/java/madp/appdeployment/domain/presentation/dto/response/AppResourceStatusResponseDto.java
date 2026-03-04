package madp.appdeployment.domain.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AppResourceStatusResponseDto(
        @JsonProperty("cpu_usage_percentage")
        Integer cpuUsagePercentage,

        @JsonProperty("memory_used")
        String memoryUsed,

        @JsonProperty("memory_total")
        String memoryTotal,

        @JsonProperty("disk_used")
        String diskUsed,

        @JsonProperty("disk_total")
        String diskTotal,

        @JsonProperty("current_instances")
        Integer currentInstances,

        @JsonProperty("available_instances")
        Integer availableInstances
) {}