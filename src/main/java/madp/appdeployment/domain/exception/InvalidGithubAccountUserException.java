package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.validation.InvalidFieldException;

public class InvalidGithubAccountUserException extends InvalidFieldException {

    public InvalidGithubAccountUserException(String message) {
        super(message);
    }
}