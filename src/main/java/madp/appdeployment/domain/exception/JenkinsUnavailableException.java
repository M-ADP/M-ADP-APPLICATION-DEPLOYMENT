package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.service.ExternalServiceUnavailableException;

public class JenkinsUnavailableException extends ExternalServiceUnavailableException {
    public JenkinsUnavailableException() {
        super("Jenkins");
    }
}
