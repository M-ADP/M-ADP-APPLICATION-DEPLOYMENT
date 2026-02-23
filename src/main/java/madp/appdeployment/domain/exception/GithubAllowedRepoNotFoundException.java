package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.resource.ResourceNotFoundException;

public class GithubAllowedRepoNotFoundException extends ResourceNotFoundException {
    public GithubAllowedRepoNotFoundException() {
        super("Github Allowed Repository를 찾을 수 없습니다.");
    }
}