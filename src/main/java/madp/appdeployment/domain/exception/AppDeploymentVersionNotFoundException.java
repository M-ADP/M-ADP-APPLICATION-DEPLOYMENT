package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.resource.ResourceNotFoundException;

public class AppDeploymentVersionNotFoundException extends ResourceNotFoundException {
    public AppDeploymentVersionNotFoundException() {
        super("해당 버전의 배포 이력을 찾을 수 없습니다.");
    }
}
