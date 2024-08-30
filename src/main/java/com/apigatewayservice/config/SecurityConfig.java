package com.apigatewayservice.config;

import com.apigatewayservice.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] ALLOWED_PATHS = {
        "/user-service/users/email/**", "/user-service/users", "/user-service/users/login",
        "/user-service/admin", "/user-service/health_check",
        "/product-service/products/**"
    };

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchange -> exchange
                .pathMatchers(ALLOWED_PATHS).permitAll()
                .pathMatchers("user-service/actuator/**").permitAll()
                .pathMatchers("product-service/admin/**").hasAuthority("ADMIN")
                .pathMatchers("order-service/**").hasAuthority("USER")
                .anyExchange().authenticated()
            )
            .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }

    @Bean
    public JwtFilter jwtWebFilter() {
        return jwtFilter;
    }
}
