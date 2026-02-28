package madp.appdeployment.global.infrastructure.internal;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class InternalRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null) return;

        HttpServletRequest request = attributes.getRequest();

        String userId = request.getHeader("X-User-Id");
        String userRole = request.getHeader("X-User-Role");

        addUserHeaders(template, userId, userRole);
    }

    private void addUserHeaders(RequestTemplate template, String userId, String userRole) {
        template.header("X-User-Id", userId);
        template.header("X-User-Role", userRole);
    }
}
