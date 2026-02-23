package madp.appdeployment.global.exception.resource;

import madp.appdeployment.global.exception.MadpBusinessException;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends MadpBusinessException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
