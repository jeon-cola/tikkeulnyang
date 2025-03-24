package com.c107.security;

import com.c107.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            // 1. Authorization 헤더에서 Access Token 추출
            String authHeader = request.getHeader("Authorization");
            String accessToken = (authHeader != null && authHeader.startsWith("Bearer "))
                    ? authHeader.substring(7) : null;

            // 2. HttpOnly 쿠키에서 Refresh Token 가져오기
            String refreshToken = Arrays.stream(request.getCookies() != null ? request.getCookies() : new Cookie[]{})
                    .filter(cookie -> "refreshToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            // 3. Access Token 검증
            if (accessToken != null && jwtUtil.validateToken(accessToken)) {
                setAuthentication(accessToken, request);
            } else if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
                // 4. Access Token이 만료되었지만 Refresh Token이 유효한 경우
                Claims claims = jwtUtil.parseClaims(refreshToken);
                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                // 5. 새로운 Access Token 생성
                String newAccessToken = jwtUtil.generateAccessToken(role, email, "nickname");

                // 6. 응답 헤더에 새로운 Access Token 추가
                response.setHeader("Authorization", "Bearer " + newAccessToken);

                setAuthentication(newAccessToken, request);
            }

        } catch (Exception e) {
            // SLF4J 포맷 오류 방지: {} 사용 대신 문자열 연결 사용
            logger.warn("JWT 인증 처리 중 예외 발생: " + e.getMessage());
        }

        chain.doFilter(request, response);
    }

    /**
     * SecurityContextHolder에 인증 정보 저장
     * - 유저가 없을 경우 예외 발생하지만, 조용히 무시하고 필터 계속 진행
     */
    private void setAuthentication(String token, HttpServletRequest request) {
        try {
            Claims claims = jwtUtil.parseClaims(token);
            String email = claims.getSubject();

            // 🔥 UserDetails 객체를 가져옴 (유저 없으면 예외 발생)
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            logger.warn("setAuthentication 실패 - 사용자 없음 또는 오류: " + e.getMessage());
        }
    }
}
