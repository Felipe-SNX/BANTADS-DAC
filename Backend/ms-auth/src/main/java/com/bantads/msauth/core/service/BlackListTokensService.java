package com.bantads.msauth.core.service;

import com.bantads.msauth.core.document.BlackListTokens;
import com.bantads.msauth.core.repository.BlackListTokensRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlackListTokensService {

    private final BlackListTokensRepository blacklistRepository;

    @Value("${spring.jwt.secret}")
    private String jwtSecret;

    public void blacklistToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
            Date expirationDate = claims.getExpiration();

            if (expirationDate.before(new Date())) {
                return;
            }

            BlackListTokens blacklistedToken = new BlackListTokens(token, expirationDate);
            blacklistRepository.save(blacklistedToken);

        } catch (Exception e) {
            log.info("Tentativa de invalidar token já inválido: {}", e.getMessage());
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistRepository.existsByToken(token);
    }
}
