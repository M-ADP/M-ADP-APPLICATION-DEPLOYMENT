package madp.appdeployment.global.util;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BuildLogMaskingUtil {

    private static final String MASK = "***";

    /**
     * 민감한 키 이름을 포함하는 변수 할당 패턴 (KEY=value, KEY: value)
     * shell set -x 출력(+ KEY=value) 포함
     */
    private static final Pattern ENV_VALUE_PATTERN = Pattern.compile(
            "(?i)((?:password|passwd|pwd|secret|token|api[_\\-]?key|access[_\\-]?key" +
                    "|private[_\\-]?key|auth(?:_token)?|credential|client[_\\-]?secret" +
                    "|registry[_\\-]?pass(?:word)?|docker[_\\-]?pass(?:word)?" +
                    "|jwt|signature)[\\s]*[=:][\\s]*)([^\\s\\n\\r\"']+)"
    );

    /** docker login --password / --password-stdin 값 */
    private static final Pattern DOCKER_PASSWORD_PATTERN = Pattern.compile(
            "(?i)(--password(?:-stdin)?[=\\s]+)([^\\s|&;\\n\\r]+)"
    );

    /** Authorization: Basic <base64> */
    private static final Pattern BASIC_AUTH_PATTERN = Pattern.compile(
            "(Authorization[:\\s]+Basic[\\s]+)([A-Za-z0-9+/=]+)"
    );

    /** Authorization: Bearer <token> */
    private static final Pattern BEARER_AUTH_PATTERN = Pattern.compile(
            "(Authorization[:\\s]+Bearer[\\s]+)([A-Za-z0-9+/=._-]+)"
    );

    /** URL 자격증명: scheme://user:password@host */
    private static final Pattern URL_CREDENTIALS_PATTERN = Pattern.compile(
            "(\\w+://[^:/\\s]+:)([^@\\s]+)(@)"
    );

    /**
     * JWT 형식 직접 탐지: eyJ로 시작하는 세 세그먼트 구조 (header.payload.signature)
     * 변수명과 관계없이 값 형태 자체로 마스킹
     */
    private static final Pattern JWT_FORMAT_PATTERN = Pattern.compile(
            "(eyJ[A-Za-z0-9_-]+\\.eyJ[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+)"
    );

    /**
     * PEM 블록: -----BEGIN ... ----- ~ -----END ... -----
     * RSA Private Key, EC PRIVATE KEY, CERTIFICATE 등
     */
    private static final Pattern PEM_BLOCK_PATTERN = Pattern.compile(
            "(-----BEGIN [A-Z ]+-----)[\\s\\S]*?(-----END [A-Z ]+-----)"
    );

    private BuildLogMaskingUtil() {}

    public static String mask(String log) {
        if (log == null || log.isBlank()) {
            return log;
        }
        String masked = maskPemBlocks(log);
        masked = applyMask(masked, ENV_VALUE_PATTERN);
        masked = applyMask(masked, DOCKER_PASSWORD_PATTERN);
        masked = applyMask(masked, BASIC_AUTH_PATTERN);
        masked = applyMask(masked, BEARER_AUTH_PATTERN);
        masked = applyJwtMask(masked);
        masked = applyUrlCredentialsMask(masked);
        return masked;
    }

    public static String maskSecretValues(String log, Collection<String> secretValues) {
        if (log == null || secretValues == null || secretValues.isEmpty()) {
            return log;
        }
        String masked = log;
        for (String value : secretValues) {
            if (value != null && !value.isBlank()) {
                masked = masked.replace(value, MASK);
            }
        }
        return masked;
    }

    private static String maskPemBlocks(String text) {
        return PEM_BLOCK_PATTERN.matcher(text).replaceAll(matchResult ->
                Matcher.quoteReplacement(matchResult.group(1) + "\n" + MASK + "\n" + matchResult.group(2))
        );
    }

    private static String applyMask(String text, Pattern pattern) {
        return pattern.matcher(text).replaceAll(matchResult ->
                Matcher.quoteReplacement(matchResult.group(1) + MASK)
        );
    }

    private static String applyJwtMask(String text) {
        return JWT_FORMAT_PATTERN.matcher(text).replaceAll(MASK);
    }

    private static String applyUrlCredentialsMask(String text) {
        return URL_CREDENTIALS_PATTERN.matcher(text).replaceAll(matchResult ->
                Matcher.quoteReplacement(matchResult.group(1) + MASK + matchResult.group(3))
        );
    }
}
