package madp.appdeployment.domain.domain.enums;

import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.exception.AccountTypeNotFoundException;

/**
 * GitHub Account 타입
 * <p>
 * Organization: facebook, google 등
 * User: john, jane 등 개인 계정
 */
@RequiredArgsConstructor
public enum GitHubAccountType {
    ORGANIZATION("organization"),
    USER("user");

    private final String accountType;

    public static GitHubAccountType from(String accountType) {
        for (GitHubAccountType type : GitHubAccountType.values()) {
            if (type.accountType.equalsIgnoreCase(accountType)) {
                return type;
            }
        }
        throw new AccountTypeNotFoundException(accountType);
    }
}