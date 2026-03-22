package madp.appdeployment.domain.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AppDeploymentListResponseDto(
        @JsonProperty("id")
        Long id,

        @JsonProperty("name")
        String name,

        @JsonProperty("pod_count")
        Integer podCount,

        @JsonProperty("exposed_port")
        Integer exposedPort,

        @JsonProperty("cpu_usage_percent")
        Double cpuUsagePercent,

        @JsonProperty("ram_usage_percent")
        Double ramUsagePercent,

        @JsonProperty("health_status")
        String healthStatus
) {}
