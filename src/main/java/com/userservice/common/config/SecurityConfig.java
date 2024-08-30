package com.userservice.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.common.dto.ResponseDTO;
import com.userservice.common.jwt.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;  // JwtFilter 주입

    private static final String[] ALLOWED_PATHS = {
        "/users/**", "/admin", "/health_check"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    ResponseDTO<Void> responseDTO = ResponseDTO.errorWithMessageAndData(
                        HttpStatus.FORBIDDEN, "Access Denied", null);
                    sendResponse(response, responseDTO);
                })
                .authenticationEntryPoint((request, response, accessDeniedException) -> {
                    ResponseDTO<Void> responseDTO = ResponseDTO.errorWithMessageAndData(
                        HttpStatus.UNAUTHORIZED, "Unauthorized: 로그인이 필요합니다", null);
                    sendResponse(response, responseDTO);
                })
            )
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers(ALLOWED_PATHS).permitAll()
                .requestMatchers(HttpMethod.POST, "/users/login").permitAll()
                .requestMatchers( "/actuator/**").permitAll()
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);  // JwtFilter 추가

        return http.build();
    }

    private void sendResponse(HttpServletResponse response, ResponseDTO<Void> responseDTO)
        throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(responseDTO.getCode());
        PrintWriter out = response.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        out.print(objectMapper.writeValueAsString(responseDTO));
        out.flush();
    }
}
