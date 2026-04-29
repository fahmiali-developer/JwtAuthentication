package com.motocare.authservice.security;

import com.motocare.authservice.entity.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "motocare-auth-super-secret-key-123456";

    public String generateToken(UserEntity user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("user_id", user.getId())          // 🔥 TAMBAH INI
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .claim("workshop_id", user.getWorkshopId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();
    }
}