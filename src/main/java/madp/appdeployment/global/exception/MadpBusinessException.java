package madp.appdeployment.global.exception;

import org.springframework.http.HttpStatus;

public class MadpBusinessException extends MadpException {
    public MadpBusinessException(String message, HttpStatus status) {
        super(message, status);
    }
}
