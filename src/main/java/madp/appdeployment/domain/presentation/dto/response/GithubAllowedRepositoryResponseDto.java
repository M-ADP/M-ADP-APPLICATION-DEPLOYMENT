package madp.appdeployment.domain.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record GithubAllowedRepositoryResponseDto(
    @JsonProperty("repository_full_name")
    String repositoryFullName,
    @JsonProperty("repository_profile")
    String repositoryProfile
) { }
