package com.skill.platform.auth.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private static final List<String> WHITELIST_PATHS = Arrays.asList(
        "/api/v1/auth/login",
        "/api/v1/auth/register",
        "/api/v1/auth/refresh",
        "/api/v1/shared/",
        "/api/v1/help-docs",
        "/api-docs",
        "/swagger-ui"
    );

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (isWhitelisted(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, "AUTH005", "Missing or invalid authorization header", 401);
            return;
        }

        String token = authHeader.substring(7);
        String userId = jwtService.validateAccessToken(token);

        if (userId == null) {
            sendError(response, "AUTH005", "Invalid or expired token", 401);
            return;
        }

        Claims claims = jwtService.extractClaims(token);
        String employeeId = claims.get("employeeId", String.class);
        List<String> roles = claims.get("roles", List.class);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userId, null,
                roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserContext.set(userId, employeeId, roles);

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }

    private boolean isWhitelisted(String path) {
        return WHITELIST_PATHS.stream().anyMatch(path::startsWith);
    }

    private void sendError(HttpServletResponse response, String code, String message, int status)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format("{\"code\":\"%s\",\"message\":\"%s\",\"data\":null}", code, message));
    }
}
