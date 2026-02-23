package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.service.ExternalServiceUnavailableException;

public class ProjectServiceUnavailableException extends ExternalServiceUnavailableException {
    public ProjectServiceUnavailableException() {
        super("프로젝트");
    }
}