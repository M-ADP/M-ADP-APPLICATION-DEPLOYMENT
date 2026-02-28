package madp.appdeployment.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MadpException extends RuntimeException {
    private final HttpStatus status;

    public MadpException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}