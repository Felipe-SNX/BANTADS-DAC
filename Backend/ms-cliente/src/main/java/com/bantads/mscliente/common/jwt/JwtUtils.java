package com.bantads.mscliente.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct; 
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component; 

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Slf4j
@Component 
public class JwtUtils {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.expiration.minutes}")
    private long expirationMinutes;
    
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(this.secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Claims getClaimsFromToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(this.key).build() 
                .parseClaimsJws(token).getBody();
    }

}
