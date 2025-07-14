package com.running.you_run.user.util;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    // SecurityConfig의 권한 설정과 이 목록을 일치시키는 것이 중요합니다.
    private static final List<String> NO_AUTH_REQUIRED_URLS = Arrays.asList(
            "/api/auth/**",
            "/api/public/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/favicon.ico",
            "/api/**",
            "/mypage/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 인증이 필요 없는 경로인지 확인
        if (isPermitted(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. 헤더에서 JWT 토큰 추출
            String jwt = resolveToken(request);

            // 3. 토큰 유효성 검사 및 인증 정보 설정
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), request.getRequestURI());
            } else {
                log.warn("유효한 JWT 토큰이 없습니다, uri: {}", request.getRequestURI());
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            // 4. 토큰 만료 예외 처리
            log.warn("만료된 JWT 토큰입니다. uri: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write("Token has expired");
        }
    }

    private boolean isPermitted(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return NO_AUTH_REQUIRED_URLS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTH_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
