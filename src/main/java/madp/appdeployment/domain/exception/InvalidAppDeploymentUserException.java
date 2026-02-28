package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.validation.InvalidFieldException;

public class InvalidAppDeploymentUserException extends InvalidFieldException {

    public InvalidAppDeploymentUserException(String message) {
        super(message);
    }
}
