package madp.appdeployment.domain.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AppDeploymentStatusResponseDto(
        @JsonProperty("name")
        String name,

        @JsonProperty("pod_count")
        Integer podCount,

        @JsonProperty("port")
        Integer port,

        @JsonProperty("cpu_usage_percentage")
        Integer cpuUsagePercentage,

        @JsonProperty("memory_usage_percentage")
        Integer memoryUsagePercentage
) {}