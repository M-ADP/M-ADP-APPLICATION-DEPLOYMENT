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
        fallback = GithubClientFallback.class
)
public interface GithubClient {

    @PostMapping("/app/installations/{installationId}/access_tokens")
    GithubAccessTokenResponse createInstallationAccessToken(
            @PathVariable String installationId,
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/orgs/{org}/members")
    List<GithubMemberResponse> getOrganizationMembers(
            @PathVariable("org") String organization,
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/repos/{owner}/{repo}/contents/{path}")
    GithubFileContentResponse getFileContent(
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable String path,
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "ref", required = false) String branch
    );
}
