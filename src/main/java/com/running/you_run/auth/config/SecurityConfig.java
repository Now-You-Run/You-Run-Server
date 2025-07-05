package com.running.you_run.auth.config;

import com.running.you_run.auth.util.JwtFilter;
import com.running.you_run.global.exception.entrypoint.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable()) // httpBasic 비활성화
                .formLogin(formLogin -> formLogin.disable()) // formLogin 비활성화
                // 세션을 사용하지 않으므로 STATELESS로 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize -> authorize
                        // JwtFilter의 NO_AUTH_REQUIRED_URLS 와 유사하게 설정
                        .requestMatchers(
                                "/api/auth/**",
                                "/mypage/**",
                                "/race-results/**",
                                "/api/track/**",
                                "/api/track",
                                "/api/record/**",
                                "/api/public/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/login/oauth2/code/**",
                                "/favicon.ico"
                        ).permitAll() // 이 경로들은 인증 없이 접근 허용
                        .anyRequest().authenticated() // 나머지 모든 요청은 반드시 인증 필요
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                );


        return http.build();
    }
}
