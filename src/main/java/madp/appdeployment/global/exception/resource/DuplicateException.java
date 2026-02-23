package madp.appdeployment.global.exception.resource;

import madp.appdeployment.global.exception.MadpBusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateException extends MadpBusinessException {
    public DuplicateException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
