package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.validation.InvalidFieldException;

public class InvalidGithubInfoException extends InvalidFieldException {

    public InvalidGithubInfoException(String message) {
        super(message);
    }
}