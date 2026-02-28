package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.validation.InvalidFieldException;

public class InvalidGitHubInstallationException extends InvalidFieldException {

    public InvalidGitHubInstallationException(String message) {
        super(message);
    }
}