package com.apigatewayservice.jwt;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements WebFilter {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private final JwtProvider jwtProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String accessToken = resolveToken(exchange);
        if (!StringUtils.hasText(accessToken) || !jwtProvider.validateToken(accessToken)) {
            return chain.filter(exchange);
        }

        Authentication authentication = jwtProvider.getAuthentication(accessToken);
        return chain.filter(exchange)
            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }

    private String resolveToken(ServerWebExchange exchange) {
        return Optional.ofNullable(
                exchange.getRequest().getCookies().getFirst(ACCESS_TOKEN_COOKIE_NAME))
            .map(cookie -> cookie.getValue())
            .orElse(null);
    }
}
