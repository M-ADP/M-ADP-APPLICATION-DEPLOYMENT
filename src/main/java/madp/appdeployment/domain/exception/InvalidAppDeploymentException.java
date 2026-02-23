package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.validation.InvalidFieldException;

public class InvalidAppDeploymentException extends InvalidFieldException {

    public InvalidAppDeploymentException(String message) {
        super(message);
    }

}