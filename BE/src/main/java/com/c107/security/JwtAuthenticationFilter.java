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
            // 쿠키에서 Access Token / Refresh Token 추출
            String accessToken = null;
            String refreshToken = null;
            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())) {
                        accessToken = cookie.getValue();
                    } else if ("refreshToken".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                    }
                }
            }

            // 1. Access Token이 유효한지 체크
            if (accessToken != null && jwtUtil.validateToken(accessToken)) {
                setAuthentication(accessToken, request);
            }
            // 2. Access Token 만료됐고, Refresh Token이 유효하면 새로운 Access Token 발급
            else if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
                Claims claims = jwtUtil.parseClaims(refreshToken);
                String email = claims.getSubject();
                String role = claims.get("role", String.class);
                String nickname = claims.get("nickname", String.class);

                // 새 Access Token 생성
                String newAccessToken = jwtUtil.generateAccessToken(role, email, nickname);

                // 재발급된 Access Token을 쿠키로 내려주기
                Cookie newAccessTokenCookie = new Cookie("accessToken", newAccessToken);
                newAccessTokenCookie.setHttpOnly(true);
                newAccessTokenCookie.setSecure(true);
                newAccessTokenCookie.setPath("/");
                // accessToken 유효시간만큼
                newAccessTokenCookie.setMaxAge( (int)(/*accessTokenExpiration*/3600L ) );
                response.addCookie(newAccessTokenCookie);

                // 다시 SecurityContext 세팅
                setAuthentication(newAccessToken, request);
            }

            // Access Token과 Refresh Token 둘 다 없거나 유효하지 않으면 -> 그냥 지나감(에러 핸들링은 Security 쪽에서)

        } catch (Exception e) {
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
