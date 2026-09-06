package dev.sorokin.eventmanager.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtManagerToken {

    private final SecretKey secretKey;
    private final long EXPIRATION_TIME;

    public JwtManagerToken(@Value("${jwt.secret-key}") String secretKey,
                           @Value("${jwt.expiration}") long expirationTime) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        EXPIRATION_TIME = expirationTime;
    }

    public String generateToken(String login) {
        return Jwts.builder()
                .subject(login)
                .signWith(secretKey)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .compact();
    }

    public String getLoginFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
