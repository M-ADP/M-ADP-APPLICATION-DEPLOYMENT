package madp.appdeployment.global.configuration;

import madp.appdeployment.global.enums.Role;
import madp.appdeployment.global.filter.MadpUserInfoExtractorFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    private static final String[] excludedPaths = {"/actuator/health", "/github/webhook"};

    @Bean
    public PathMatcher pathMatcher() {return new AntPathMatcher();}

    @Bean
    public ObjectMapper objectMapper() {return new ObjectMapper();}

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .anonymous(anonymous -> anonymous
                        .principal(Role.GUEST.name())
                        .authorities(Role.GUEST.getValue())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/github/webhook", "/apps", "/apps/github").permitAll()
                        .requestMatchers("/login").denyAll()
                        .anyRequest().denyAll()
                )
                .addFilterAfter(new MadpUserInfoExtractorFilter(pathMatcher(), excludedPaths), SecurityContextHolderFilter.class);

        return http.build();
    }
}
