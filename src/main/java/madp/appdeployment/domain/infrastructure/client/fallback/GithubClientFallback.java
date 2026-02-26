package madp.appdeployment.domain.infrastructure.client.fallback;

import madp.appdeployment.domain.exception.GithubServiceUnavailableException;
import madp.appdeployment.domain.infrastructure.client.GithubClient;
import madp.appdeployment.domain.infrastructure.client.response.GithubAccessTokenResponse;
import madp.appdeployment.domain.infrastructure.client.response.GithubFileContentResponse;
import madp.appdeployment.domain.infrastructure.client.response.GithubMemberResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GithubClientFallback implements GithubClient {
    @Override
    public GithubAccessTokenResponse createInstallationAccessToken(String installationId, String authorization) {
        throw new GithubServiceUnavailableException();
    }

    @Override
    public List<GithubMemberResponse> getOrganizationMembers(String organization, String authorization) {
        throw new GithubServiceUnavailableException();
    }

    @Override
    public GithubFileContentResponse getFileContent(String owner, String repo, String path, String authorization, String branch) {
        throw new GithubServiceUnavailableException();
    }
}
