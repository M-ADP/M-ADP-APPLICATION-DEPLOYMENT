package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.resource.ResourceNotFoundException;

public class AppBuildNotFoundException extends ResourceNotFoundException {
    public AppBuildNotFoundException() {
        super("빌드 이력을 찾을 수 없습니다.");
    }
}
