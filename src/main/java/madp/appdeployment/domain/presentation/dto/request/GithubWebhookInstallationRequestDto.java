package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GithubWebhookInstallationRequestDto(
        @JsonProperty("action")
        String action,

        @JsonProperty("installation")
        Installation installation,

        @JsonProperty("repositories")
        List<GithubRepositoryRequestDto> repositories,

        @JsonProperty("repositories_added")
        List<GithubRepositoryRequestDto> repositoriesAdded,

        @JsonProperty("repositories_removed")
        List<GithubRepositoryRequestDto> repositoriesRemoved
) {
    public record Installation(
            @JsonProperty("id")
            Long id,
            
            @JsonProperty("account")
            Account account
    ) {}
    
    public record Account(
            @JsonProperty("id")
            Long id,
            
            @JsonProperty("login")
            String login,
            
            @JsonProperty("type")
            String type,
            
            @JsonProperty("avatar_url")
            String avatarUrl
    ) {}
}
