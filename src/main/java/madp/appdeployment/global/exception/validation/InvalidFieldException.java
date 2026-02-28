package madp.appdeployment.global.exception.validation;

import madp.appdeployment.global.exception.MadpBusinessException;
import org.springframework.http.HttpStatus;

public class InvalidFieldException extends MadpBusinessException {
    public InvalidFieldException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
