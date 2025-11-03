package com.bantads.msauth.core.jwt;

import io.jsonwebtoken.Claims; 
import io.jsonwebtoken.JwtException; 
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component; 
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component 
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    public static final String JWT_BEARER = "Bearer ";
    public static final String JWT_AUTHORIZATION = "Authorization";

    private final JwtUserDetailsService detailsService;
    private final JwtUtils jwtUtils; 

    public JwtAuthorizationFilter(JwtUserDetailsService detailsService, JwtUtils jwtUtils) {
        this.detailsService = detailsService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String header = request.getHeader(JWT_AUTHORIZATION);

        if (header == null || !header.startsWith(JWT_BEARER)) {
            log.trace("JWT Token está nulo ou não começa com 'Bearer'."); // Use trace para logs menos importantes
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(JWT_BEARER.length());

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            
            try {
                Claims claims = jwtUtils.getClaimsFromToken(token);
                String username = claims.getSubject();

                if (username != null) {
                    toAuthentication(request, username);
                }
                
            } catch (JwtException e) {
                log.warn("Falha ao validar o token JWT: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private void toAuthentication(HttpServletRequest request, String username) {
        UserDetails userDetails = detailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken
                .authenticated(userDetails, null, userDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}