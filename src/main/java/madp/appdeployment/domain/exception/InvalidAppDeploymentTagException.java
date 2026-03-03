package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.MadpBusinessException;
import org.springframework.http.HttpStatus;

public class InvalidAppDeploymentTagException extends MadpBusinessException {

    public InvalidAppDeploymentTagException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}