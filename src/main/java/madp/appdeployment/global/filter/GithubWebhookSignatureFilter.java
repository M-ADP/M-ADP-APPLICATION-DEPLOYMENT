package madp.appdeployment.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.exception.WebhookSignatureInvalidException;
import madp.appdeployment.domain.infrastructure.github.webhook.GithubWebhookSignatureVerifier;
import madp.appdeployment.global.properties.GithubWebhookProperties;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.nio.charset.StandardCharsets;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Order(1)
public class GithubWebhookSignatureFilter extends OncePerRequestFilter {
    private final GithubWebhookProperties githubWebhookProperties;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        return !("/github/webhook".equals(path) && HttpMethod.POST.name().equals(method));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 캐싱된 request 에서 body 읽기
        CachedBodyHttpServletRequest cachedRequest = (CachedBodyHttpServletRequest) request;
        byte[] requestBody = cachedRequest.getCachedBody();
        String signature = cachedRequest.getHeader("X-Hub-Signature-256");

        // 서명 검증
        if (!GithubWebhookSignatureVerifier.verifySha256(requestBody, githubWebhookProperties.getWebhookSecret(), signature)) {
            throw new WebhookSignatureInvalidException();
        }

        filterChain.doFilter(request, response);
    }
}
