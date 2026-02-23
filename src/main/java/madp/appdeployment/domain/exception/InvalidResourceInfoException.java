package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.validation.InvalidFieldException;

public class InvalidResourceInfoException extends InvalidFieldException {

    public InvalidResourceInfoException(String message) {
        super(message);
    }
}