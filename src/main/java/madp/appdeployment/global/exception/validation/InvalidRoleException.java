package madp.appdeployment.global.exception.validation;

public class InvalidRoleException extends InvalidFieldException {
    public InvalidRoleException(String roleString) {
        super(roleString + "역할은 지원하지 않습니다.");
    }
}
