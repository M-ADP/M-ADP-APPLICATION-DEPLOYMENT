package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.resource.ResourceNotFoundException;

public class AppDeploymentNotFoundException extends ResourceNotFoundException {
    public AppDeploymentNotFoundException() {
        super("App Deployment를 찾을 수 없습니다.");
    }
}
