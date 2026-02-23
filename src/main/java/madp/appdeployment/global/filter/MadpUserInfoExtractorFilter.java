package madp.appdeployment.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import madp.appdeployment.global.enums.Role;
import madp.appdeployment.global.annotation.Trace;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@Trace
public class MadpUserInfoExtractorFilter extends OncePerRequestFilter {
    private final PathMatcher pathMatcher;
    private final String[] excludedPaths;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String userIdRequestHeader = request.getHeader("X-User-Id");
        String roleRequestHeader = request.getHeader("X-User-Role");

        if(userIdRequestHeader == null ||  roleRequestHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = Long.valueOf(userIdRequestHeader);
        Role role = Role.of(roleRequestHeader);

        Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(role.getValue()));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return Arrays.stream(excludedPaths)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
