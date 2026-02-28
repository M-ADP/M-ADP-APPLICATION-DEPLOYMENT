package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.resource.ResourceNotFoundException;

public class InstallationNotFoundException extends ResourceNotFoundException {

    public InstallationNotFoundException(String message) {
        super(message);
    }
}