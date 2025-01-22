package org.example.workspace.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.workspace.util.JwtUtil;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        List<String> patternList = List.of(
                HttpMethod.POST + "/api/v1/login",
                HttpMethod.POST + "/api/v1/login-refresh",
                HttpMethod.POST + "/api/v1/users",
                HttpMethod.POST + "/api/v1/users/verify",
                HttpMethod.POST + "/api/v1/users/recover",
                HttpMethod.PATCH + "/api/v1/users/password",
                HttpMethod.GET + "/api/v1/users/duplicate"
        );

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        String requestIdentifier = method + requestURI;

        return patternList.stream().anyMatch(requestIdentifier::equals);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            flushResponseWithStatusUnauthorized(response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            String username = jwtUtil.extractSubject(token, JwtUtil.TokenType.ACCESS);
            if (username == null) {
                flushResponseWithStatusUnauthorized(response);
                return;
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails.getUsername(), JwtUtil.TokenType.ACCESS)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException e) {
            flushResponseWithStatusUnauthorized(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static void flushResponseWithStatusUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.flushBuffer();
    }
}
