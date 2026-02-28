package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.resource.ResourceNotFoundException;

public class AccountTypeNotFoundException extends ResourceNotFoundException {
    public AccountTypeNotFoundException(String accountType) {
        super(accountType + "의 계정 타입은 존재하지 않습니다.");
    }
}
