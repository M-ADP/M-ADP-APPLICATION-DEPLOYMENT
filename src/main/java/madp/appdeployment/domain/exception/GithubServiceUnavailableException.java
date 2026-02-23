package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.service.ExternalServiceUnavailableException;

public class GithubServiceUnavailableException extends ExternalServiceUnavailableException {
    public GithubServiceUnavailableException() {
        super("Github");
    }
}
