package madp.appdeployment.domain.infrastructure.client;

import madp.appdeployment.domain.infrastructure.client.fallback.GithubClientFallback;
import madp.appdeployment.domain.infrastructure.client.response.GithubAccessTokenResponse;
import madp.appdeployment.domain.infrastructure.client.response.GithubFileContentResponse;
import madp.appdeployment.domain.infrastructure.client.response.GithubMemberResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "github-client",
        url = "https://api.github.com",
        fallback = GithubClientFallback.class
)
public interface GithubClient {

    @PostMapping("/app/installations/{installationId}/access_tokens")
    GithubAccessTokenResponse createInstallationAccessToken(
            @PathVariable("installationId") String installationId,
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/orgs/{org}/members")
    List<GithubMemberResponse> getOrganizationMembers(
            @PathVariable("org") String organization,
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/repos/{owner}/{repo}/contents/{path}")
    GithubFileContentResponse getFileContent(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @PathVariable("path") String path,
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "ref", required = false) String branch
    );
}
