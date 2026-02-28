package madp.appdeployment.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import madp.appdeployment.global.exception.validation.InvalidRoleException;

import java.util.Arrays;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("ROLE_GUEST"),
    PARTIAL_AUTH("ROLE_PARTIAL_AUTH"),
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String value;
    private static final Map<String, Role> TYPE_MAP =
            Arrays.stream(values()).collect(toMap(type -> type.value, identity()));

    public static Role of(String roleString) {
        Role role = TYPE_MAP.get(roleString);
        if (role == null) {
            throw new InvalidRoleException(roleString);
        }
        return role;
    }
}
