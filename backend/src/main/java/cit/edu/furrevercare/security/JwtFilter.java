package cit.edu.furrevercare.security;

import cit.edu.furrevercare.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        logger.debug("JwtFilter initialized with JwtUtil: " + jwtUtil + ", UserService: " + userService);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.debug("JwtFilter processing request: " + request.getRequestURI() + ", Method: " + request.getMethod());

        try {
            // Skip authentication for /api/auth/** endpoints
            String path = request.getRequestURI();
            if (path.startsWith("/api/auth/")) {
                logger.debug("Skipping authentication for path: " + path);
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");
            String userId = null;
            String jwt = null;

            // Extract JWT from Authorization header
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                logger.debug("JWT extracted: " + jwt);
                try {
                    userId = jwtUtil.extractUserId(jwt);
                    if (userId == null) {
                        logger.warn("No user ID extracted from token");
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid token: No user ID found");
                        return;
                    }
                    logger.debug("User ID extracted: " + userId);
                } catch (Exception e) {
                    logger.error("Error extracting user ID from token: " + e.getMessage(), e);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid token: " + e.getMessage());
                    return;
                }
            } else {
                logger.warn("Missing or invalid Authorization header: " + authHeader);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Missing or invalid Authorization header");
                return;
            }

            // Validate token and set authentication context
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    // Check if user exists
                    logger.debug("Checking user existence for userId: " + userId);
                    if (userService.getUserById(userId) == null) {
                        logger.warn("User not found for userId: " + userId);
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "User not found for userId: " + userId);
                        return;
                    }

                    // Validate token
                    logger.debug("Validating token for userId: " + userId);
                    if (!jwtUtil.validateToken(jwt, userId)) {
                        logger.warn("Token validation failed for userId: " + userId);
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token validation failed (expired or invalid)");
                        return;
                    }

                    // Set authentication in SecurityContextHolder
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userId, null, Collections.singletonList(new SimpleGrantedAuthority("USER")));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Authentication set for userId: " + userId);
                } catch (Exception e) {
                    logger.error("Error validating token for userId: " + userId, e);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authentication error: " + e.getMessage());
                    return;
                }
            } else {
                logger.debug("No authentication set: userId=" + userId + ", existing auth=" + SecurityContextHolder.getContext().getAuthentication());
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Unexpected error in JwtFilter for request: " + request.getRequestURI(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected server error: " + e.getMessage());
        }
    }
}