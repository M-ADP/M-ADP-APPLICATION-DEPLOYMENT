package madp.appdeployment.domain.infrastructure.client.response;

public record GithubMemberResponse(
        Long id,
        String login,
        String type
) {}