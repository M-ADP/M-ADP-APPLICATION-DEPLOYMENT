package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubWebhookOrganizationRequestDto(
        @JsonProperty(value = "action", required = true)
        String action,
        @JsonProperty(value = "membership.user.id", required = true)
        Long githubId,
        @JsonProperty(value = "membership.user.login", required = true)
        String githubName,
        @JsonProperty(value = "membership.user.type", required = true)
        String githubType,
        @JsonProperty(value = "organization.id", required = true)
        Long organizationId
) {}
