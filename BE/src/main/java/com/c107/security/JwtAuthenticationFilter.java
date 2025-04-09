package com.c107.security;

import com.c107.common.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY_MONITOR");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        try {
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

            if (accessToken != null && jwtUtil.validateToken(accessToken)) {
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    setAuthentication(accessToken, request, true);
                }
            } else if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
                Claims claims = jwtUtil.parseClaims(refreshToken);
                String email = claims.getSubject();
                String role = claims.get("role", String.class);
                String nickname = claims.get("nickname", String.class);

                String newAccessToken = jwtUtil.generateAccessToken(role, email, nickname);

                Cookie newAccessTokenCookie = new Cookie("accessToken", newAccessToken);
                newAccessTokenCookie.setHttpOnly(true);
                newAccessTokenCookie.setSecure(true);
                newAccessTokenCookie.setPath("/");
                newAccessTokenCookie.setMaxAge(3600); // 1시간
                response.addCookie(newAccessTokenCookie);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    setAuthentication(newAccessToken, request, true);
                }
            }

        } catch (Exception e) {
            logger.warn("JWT 인증 처리 중 예외 발생: {}", e.getMessage());
        }

        chain.doFilter(request, response);
    }

    /**
     * 최초 자동 로그인 감지 시 보안 로그 남기기
     */
    private void setAuthentication(String token, HttpServletRequest request, boolean logIfFirstLogin) {
        try {
            Claims claims = jwtUtil.parseClaims(token);
            String email = claims.getSubject();
            String role = claims.get("role", String.class);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // ✅ 최초 자동 로그인 여부 체크 (세션)
            boolean alreadyLogged = Boolean.TRUE.equals(
                    request.getSession().getAttribute("alreadyAutoLoggedIn"));

            if (logIfFirstLogin && !alreadyLogged) {
                logger.info("[자동 로그인] 최초 인증 성공 - email: {}, role: {}", email, role);

                Map<String, Object> logEntry = new HashMap<>();
                logEntry.put("event_type", "auto_login");
                logEntry.put("email", email);
                logEntry.put("role", role);
                logEntry.put("ip", request.getRemoteAddr());
                logEntry.put("user_agent", request.getHeader("User-Agent"));

                securityLogger.info(objectMapper.writeValueAsString(logEntry));

                // ✅ 세션에 플래그 저장 → 이후 중복 로그 방지
                request.getSession().setAttribute("alreadyAutoLoggedIn", true);
            }

        } catch (Exception e) {
            logger.warn("setAuthentication 실패: {}", e.getMessage());
        }
    }
}
