package com.trackit.payment.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    // ✅ Updated public paths list
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/api/payments/razorpay-key",
        "/api/payments/create-order",
        "/api/payments/verify",
        "/api/payments/order",              // ✅ ADDED
        "/api/orders/calculate-shipping",
        "/actuator/health",
        "/actuator/info",
        "/error"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        log.debug("🔍 Processing request: {} {}", method, requestPath);

        // ✅ Skip public endpoints
        if (isPublicEndpoint(requestPath)) {
            log.debug("✅ Public endpoint, skipping auth: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Skip OPTIONS
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader("Authorization");

            // ✅ No token - just continue, Spring Security will handle
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("⚠️ No auth header for: {}", requestPath);
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = authHeader.substring(7);
            log.debug("🔑 JWT token found, validating...");

            // ✅ Invalid token - continue, let Spring Security reject it
            if (!jwtUtils.validateToken(jwt)) {
                log.warn("❌ Invalid or expired JWT token for: {}", requestPath);
                filterChain.doFilter(request, response); // ✅ FIXED - was sending 401 directly
                return;
            }

            String username = jwtUtils.extractUsername(jwt);
            String role = jwtUtils.extractRole(jwt);

            log.debug("✅ Valid token for user: {}, role: {}", username, role);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String userRole = (role != null) ? role : "USER";

                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole))
                    );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("✅ Authentication set for user: {}", username);
            }

        } catch (Exception e) {
            log.error("❌ JWT authentication failed: {}", e.getMessage());
            // ✅ Don't block - let Spring Security handle it
        }

        filterChain.doFilter(request, response);
    }

    // ✅ Fixed isPublicEndpoint
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_PATHS.stream()
                .anyMatch(publicPath ->
                    path.equals(publicPath) ||
                    path.startsWith(publicPath + "/") ||
                    path.startsWith(publicPath)   // ✅ covers /api/payments/order/ORD-xxx
                );
    }
}