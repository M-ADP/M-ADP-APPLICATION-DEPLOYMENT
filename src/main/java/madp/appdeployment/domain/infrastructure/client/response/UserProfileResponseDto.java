package madp.appdeployment.domain.infrastructure.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserProfileResponseDto(
        Long id,
        String nickname,
        @JsonProperty("github_id")
        String githubId,
        String profile
) {}

