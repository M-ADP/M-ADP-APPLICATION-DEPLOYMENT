package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.MadpBusinessException;
import org.springframework.http.HttpStatus;

public class ProjectResourceLockConflictException extends MadpBusinessException {
    public ProjectResourceLockConflictException() {
        super("동일 프로젝트 리소스 변경 요청이 진행 중입니다. 잠시 후 다시 시도해주세요.", HttpStatus.CONFLICT);
    }
}
