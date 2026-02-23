package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GithubWebhookCommitRequestDto(
        @JsonProperty("id")
        String id,
        
        @JsonProperty("added")
        List<String> added,
        
        @JsonProperty("removed")
        List<String> removed,
        
        @JsonProperty("modified")
        List<String> modified
) {}