package madp.appdeployment.domain.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.exception.AccountTypeNotFoundException;

/**
 * GitHub User 타입
 * <p>
 * User: 실제 사람 계정
 * Bot: GitHub App이나 봇 계정
 */
@Getter
@RequiredArgsConstructor
public enum GithubUserType {
    USER("User"),
    BOT("Bot");

    private final String userType;

    public static GithubUserType from(String userType) {
        for (GithubUserType type : GithubUserType.values()) {
            if (type.userType.equals(userType)) {
                return type;
            }
        }
        throw new AccountTypeNotFoundException(userType);
    }
}