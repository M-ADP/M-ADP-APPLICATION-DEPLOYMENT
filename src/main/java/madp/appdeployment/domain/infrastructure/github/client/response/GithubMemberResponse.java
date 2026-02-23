package madp.appdeployment.domain.infrastructure.github.client.response;

public record GithubMemberResponse(
        Long id,
        String login,
        String type
) {}