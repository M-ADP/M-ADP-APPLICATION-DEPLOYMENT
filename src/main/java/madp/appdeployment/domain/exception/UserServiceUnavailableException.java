package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.service.ExternalServiceUnavailableException;

public class UserServiceUnavailableException extends ExternalServiceUnavailableException {
    public UserServiceUnavailableException() {
        super("사용자");
    }
}
