package madp.appdeployment.domain.infrastructure.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AppDeploymentResourceStatusResponseDto(
        @JsonProperty("message")
        String message,

        @JsonProperty("data")
        List<AppResourceDto> data
) {
    public record AppResourceDto(
            @JsonProperty("app_id")
            String appId,

            @JsonProperty("project_id")
            String projectId,

            @JsonProperty("cpu")
            ResourceMetricDto cpu,

            @JsonProperty("memory")
            ResourceMetricDto memory,

            @JsonProperty("disk")
            ResourceMetricDto disk,

            @JsonProperty("instance")
            InstanceMetricDto instance
    ) {}

    public record ResourceMetricDto(
            @JsonProperty("limit")
            String limit,

            @JsonProperty("used")
            String used,

            @JsonProperty("percentage")
            Integer percentage,

            @JsonProperty("unit")
            String unit
    ) {}

    public record InstanceMetricDto(
            @JsonProperty("limit")
            Integer limit,

            @JsonProperty("used")
            Integer used,

            @JsonProperty("percentage")
            Integer percentage
    ) {}
}