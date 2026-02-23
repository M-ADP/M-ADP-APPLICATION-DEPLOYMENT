package madp.appdeployment.domain.infrastructure.github.webhook;

import madp.appdeployment.domain.exception.WebhookSignatureInvalidException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public final class GithubWebhookSignatureVerifier {

    public static boolean verifySha256(byte[] rawBody, String secret, String xHubSignature256Header) {
        if (secret == null || secret.isBlank()) return false;
        if (xHubSignature256Header == null || !xHubSignature256Header.startsWith("sha256=")) return false;

        String expectedHex = hmacHex(secret, rawBody);
        String providedHex = xHubSignature256Header.substring("sha256=".length());

        return constantTimeEqualsIgnoreCase(expectedHex, providedHex);
    }

    private static String hmacHex(String secret, byte[] data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(data);
            return toHexLower(digest);
        }
        catch (NoSuchAlgorithmException | InvalidKeyException e){
            throw new WebhookSignatureInvalidException();
        }
    }

    private static String toHexLower(byte[] bytes) {
        char[] hex = "0123456789abcdef".toCharArray();
        char[] out = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            out[i * 2] = hex[v >>> 4];
            out[i * 2 + 1] = hex[v & 0x0F];
        }
        return new String(out);
    }

    private static boolean constantTimeEqualsIgnoreCase(String a, String b) {
        if (a == null || b == null) return false;
        a = a.toLowerCase();
        b = b.toLowerCase();
        if (a.length() != b.length()) return false;

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}