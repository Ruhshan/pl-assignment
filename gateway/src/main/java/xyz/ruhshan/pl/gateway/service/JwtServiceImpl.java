package xyz.ruhshan.pl.gateway.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    private final Algorithm algorithm;
    private final int expiresAfter;

    public JwtServiceImpl(@Value("${jwt.key}") String jwtKey, @Value("${jwt.expiresAfter}") int expiresAfter) {
        this.algorithm = Algorithm.HMAC256(jwtKey);
        this.expiresAfter = expiresAfter;
    }

    @Override
    public String generateToken(String email) {
        Date expiresAt = new Date(System.currentTimeMillis()+expiresAfter);
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(expiresAt)
                .sign(this.algorithm);

    }
}
