package com.bantads.mscliente.common.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    public static final String JWT_BEARER = "Bearer ";
    public static final String JWT_AUTHORIZATION = "Authorization";

    private final JwtUtils jwtUtils;

    public JwtAuthorizationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        final String header = request.getHeader(JWT_AUTHORIZATION);

        if (header == null || !header.startsWith(JWT_BEARER)) {
            log.trace("JWT Token está nulo ou não começa com 'Bearer'.");
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(JWT_BEARER.length());

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims claims = jwtUtils.getClaimsFromToken(token);
                toAuthentication(request, claims); 
                
            } catch (JwtException e) {
                log.warn("Falha ao validar o token JWT: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    private void toAuthentication(HttpServletRequest request, Claims claims) {
        
        String username = claims.getSubject();
        
        String role = claims.get("role", String.class); 

        if (role == null) {
            log.warn("Token JWT inválido! Não contém a claim 'role'.");
            SecurityContextHolder.clearContext();
            return;
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        UserDetails userDetails = new User(username, "", authorities);

        UsernamePasswordAuthenticationToken authToken = UsernamePasswordAuthenticationToken
                .authenticated(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}