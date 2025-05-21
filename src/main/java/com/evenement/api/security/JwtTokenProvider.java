package com.evenement.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey jwtSecret;
    private final long jwtExpirationInMs;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {
        logger.info("Longueur de la clé JWT (caractères) : {}", secret.length());
        logger.info("Longueur de la clé JWT (bits) : {}", secret.getBytes(StandardCharsets.UTF_8).length * 8);
        this.jwtSecret = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationInMs = expiration;
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecret, SignatureAlgorithm.HS512)
                .compact();
        logger.debug("Token généré pour: {} , token: {}", username, token);
        return token;
    }

    public String getUsernameFromJWT(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            logger.debug("Username extrait du token: {}", claims.getSubject());
            return claims.getSubject();
        } catch (JwtException e) {
            logger.error("Erreur lors de l'extraction du username: {}", e.getMessage(), e);
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token);
            logger.debug("Token validé avec succès: {}", token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Erreur de validation du token JWT: {}", e.getMessage(), e);
            return false;
        }
    }
}