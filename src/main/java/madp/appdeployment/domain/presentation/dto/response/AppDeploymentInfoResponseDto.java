package madp.appdeployment.domain.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AppDeploymentInfoResponseDto(
        @JsonProperty("app_id")
        Long appId,

        @JsonProperty("port")
        Integer port,

        @JsonProperty("resource_use_percentage")
        Integer resourceUsePercentage,

        @JsonProperty("github_repository_url")
        String githubRepositoryUrl,

        @JsonProperty("status")
        String status
) {}