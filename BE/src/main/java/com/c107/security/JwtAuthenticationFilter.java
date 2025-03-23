package com.c107.security;

import com.c107.common.util.JwtUtil;
import com.c107.security.CustomUserDetailsService;
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
    private final CustomUserDetailsService userDetailsService; // 🔥 UserDetailsService 추가

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

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

        chain.doFilter(request, response);
    }

    /**
     * SecurityContextHolder에 인증 정보 저장
     */
    private void setAuthentication(String token, HttpServletRequest request) {
        Claims claims = jwtUtil.parseClaims(token);
        String email = claims.getSubject();

        // 🔥 UserDetails 객체를 가져옴
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
