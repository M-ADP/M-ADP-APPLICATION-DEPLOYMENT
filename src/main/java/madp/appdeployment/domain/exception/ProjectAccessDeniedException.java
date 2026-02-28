package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.MadpBusinessException;
import org.springframework.http.HttpStatus;

public class ProjectAccessDeniedException extends MadpBusinessException {
    public ProjectAccessDeniedException() {
        super("해당 프로젝트에 대한 접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }
}