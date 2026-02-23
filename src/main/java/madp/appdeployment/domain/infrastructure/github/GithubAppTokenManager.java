package madp.appdeployment.domain.infrastructure.github;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.exception.GithubPrivateKeyInvalidException;
import madp.appdeployment.domain.infrastructure.github.client.GithubClient;
import madp.appdeployment.global.properties.GithubWebhookProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.Security;
import java.io.StringReader;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class GithubAppTokenManager {
    
    private final GithubWebhookProperties githubWebhookProperties;
    private final GithubClient githubClient;
    private final RedisTemplate<String, String> redisTemplate;

    // @RedisHash로 빼긴 하나의 키밖에 없어서 RedisTemplate 으로 제어
    private static final String GITHUB_APP_JWT_KEY = "github:app:jwt";

    public String getInstallationAccessToken(String installationId) {
        String appJwt = getGithubAppAccessToken();
        return "Bearer " + githubClient.createInstallationAccessToken(installationId, appJwt).token();
    }

    public String getGithubAppAccessToken() {
        String cachedJwt = redisTemplate.opsForValue().get(GITHUB_APP_JWT_KEY);
        if (cachedJwt != null) {
            return "Bearer " + cachedJwt;
        }
        
        String newJwt = generateAppAccessToken();
        redisTemplate.opsForValue().set(GITHUB_APP_JWT_KEY, newJwt, Duration.ofMinutes(9));
        
        return "Bearer " + newJwt;
    }
    
    private String generateAppAccessToken() {
        PrivateKey privateKey = parsePrivateKey(githubWebhookProperties.getPrivateKey());
            
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(600);

        return Jwts.builder()
                .header().add("alg", "RS256").and()
                .issuer(githubWebhookProperties.getAppId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(privateKey)
                .compact();
    }
    
    private PrivateKey parsePrivateKey(String privateKeyContent) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            
            PEMParser pemParser = new PEMParser(new StringReader(privateKeyContent));
            Object pemObject = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            
            return converter.getKeyPair((PEMKeyPair) pemObject).getPrivate();
        } catch(Exception e) {
            throw new GithubPrivateKeyInvalidException();
        }
    }
}
