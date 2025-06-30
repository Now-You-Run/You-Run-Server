package com.running.you_run.auth.util;

import com.running.you_run.auth.Enum.UserRole;
import com.running.you_run.auth.payload.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {

    // 상수는 명확하게 역할 분리
    private static final String AUTHORITIES_KEY = "auth"; // 권한 정보를 담는 Key

    private final Key key;
    private final long accessTokenValidityMilliSeconds;
    private final long refreshTokenValidityMilliSeconds;

    /**
     * 생성자에서 시크릿 키와 토큰 유효 시간을 주입받고, Key 객체를 즉시 생성합니다.
     * @PostConstruct가 불필요해집니다.
     */
    public TokenProvider(
            @Value("${jwt.secret_key}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValiditySeconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValiditySeconds) {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityMilliSeconds = accessTokenValiditySeconds * 1000;
        this.refreshTokenValidityMilliSeconds = refreshTokenValiditySeconds * 1000;
    }

    /**
     * 사용자 정보(이메일, 역할)를 기반으로 Access Token과 Refresh Token을 생성합니다.
     */
    public TokenDto createToken(String email, UserRole role) {
        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + this.accessTokenValidityMilliSeconds);
        String accessToken = Jwts.builder()
                .setSubject(email) // 표준 클레임인 subject에 이메일 저장
                .claim(AUTHORITIES_KEY, role.toString()) // 커스텀 클레임으로 권한 정보 저장
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512) // HS256보다 강력한 HS512 권장
                .compact();

        // Refresh Token 생성 (보통 Refresh Token에는 만료 시간 외에 다른 정보는 불필요할 수 있음)
        Date refreshTokenExpiresIn = new Date(now + this.refreshTokenValidityMilliSeconds);
        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return new TokenDto(accessToken, refreshToken);
    }

    /**
     * JWT 토큰을 복호화하여 토큰에 담겨 있는 인증 정보를 꺼냅니다.
     */
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 토큰의 유효성을 검증합니다. 만료된 경우 ExpiredJwtException을 발생시킵니다.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
            throw e; // 예외를 그대로 던져서 필터가 처리하도록 함
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * 토큰에서 클레임 정보를 추출합니다.
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰에서도 클레임 정보는 필요할 수 있으므로 반환합니다.
            return e.getClaims();
        }
    }
}
