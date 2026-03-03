package madp.appdeployment.domain.infrastructure.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PodLogsResponseDto(
        @JsonProperty("message")
        String message,

        @JsonProperty("data")
        LogDataDto data
) {
    public record LogDataDto(
            @JsonProperty("deployment_name")
            String deploymentName,

            @JsonProperty("namespace")
            String namespace,

            @JsonProperty("pod_logs")
            List<PodLogDto> podLogs
    ) {}

    public record PodLogDto(
            @JsonProperty("pod_name")
            String podName,

            @JsonProperty("logs")
            String logs
    ) {}
}