package madp.appdeployment.global.exception;

import org.springframework.http.HttpStatus;

public class MadpSystemError extends MadpException {
    public MadpSystemError(String message, HttpStatus status) {
        super(message, status);
    }
}
