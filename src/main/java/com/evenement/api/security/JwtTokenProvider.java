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
           if (secret.length() < 64) {
               logger.warn("La clé JWT est trop courte (< 64 caractères). Génération d'une clé sécurisée recommandée.");
           }
           logger.info("Longueur de la clé JWT (caractères) : {}", secret.length());
           logger.info("Longueur de la clé JWT (bits) : {}", secret.getBytes(StandardCharsets.UTF_8).length * 8);
           this.jwtSecret = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
           this.jwtExpirationInMs = expiration;
       }

       public String generateToken(Authentication authentication) {
           if (authentication == null || authentication.getName() == null) {
               logger.error("L'objet Authentication est null ou n'a pas de nom");
               throw new IllegalArgumentException("Authentication invalide");
           }
           String username = authentication.getName();
           Date now = new Date();
           Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

           String token = Jwts.builder()
                   .setSubject(username)
                   .setIssuedAt(now)
                   .setExpiration(expiryDate)
                   .signWith(jwtSecret, SignatureAlgorithm.HS512)
                   .compact();
           logger.debug("Token généré pour : {}", username);
           return token;
       }

       public String getUsernameFromJWT(String token) {
           try {
               Claims claims = Jwts.parserBuilder()
                       .setSigningKey(jwtSecret)
                       .build()
                       .parseClaimsJws(token)
                       .getBody();
               String username = claims.getSubject();
               if (username == null) {
                   logger.error("Le sujet du token est null");
                   throw new JwtException("Sujet du token invalide");
               }
               logger.debug("Username extrait du token : {}", username);
               return username;
           } catch (JwtException e) {
               logger.error("Erreur lors de l'extraction du username : {}", e.getMessage());
               throw new JwtException("Token JWT invalide : " + e.getMessage());
           }
       }

       public boolean validateToken(String token) {
           try {
               Jwts.parserBuilder()
                       .setSigningKey(jwtSecret)
                       .build()
                       .parseClaimsJws(token);
               logger.debug("Token validé avec succès");
               return true;
           } catch (JwtException | IllegalArgumentException e) {
               logger.error("Erreur de validation du token JWT : {}", e.getMessage());
               return false;
           }
       }
   }