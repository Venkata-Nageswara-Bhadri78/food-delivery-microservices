package com.natixis.apigateway.security;

import com.natixis.apigateway.enums.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public Object extractUserId(String token) {
        return extractClaims(token).get("userId");
    }

    public UserType extractUserType(String token) {
        String userType = extractClaims(token).get("userType", String.class);
        return UserType.valueOf(userType);
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getSubject() != null
                    && !claims.getSubject().isBlank()
                    && claims.get("userId") != null
                    && claims.get("userType") != null;
        } catch (Exception ex) {
            return false;
        }
    }
}
