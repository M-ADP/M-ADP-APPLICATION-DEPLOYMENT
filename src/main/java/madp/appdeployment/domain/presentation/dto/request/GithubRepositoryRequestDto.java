package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubRepositoryRequestDto(
        Long id,
        String name,
        @JsonProperty("full_name")
        String fullName
) {}
