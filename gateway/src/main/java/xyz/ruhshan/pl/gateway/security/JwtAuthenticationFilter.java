package xyz.ruhshan.pl.gateway.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String jwtHeader;
    private final String tokenPrefix;
    private final JWTVerifier jwtVerifier;

    public JwtAuthenticationFilter(@Value("${jwt.header}") String jwtHeader,
                                   @Value("${jwt.tokenPrefix}") String tokenPrefix,
                                   @Value("${jwt.key}") String key) {
        this.jwtHeader = jwtHeader;
        this.tokenPrefix = tokenPrefix;

        this.jwtVerifier = JWT.require(Algorithm.HMAC256(key)).build();
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String header = request.getHeader(jwtHeader);

        if (header == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authToken = header.replace(tokenPrefix,"").strip();
        DecodedJWT decodedJWT = verifyToken(authToken);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                decodedJWT.getSubject(), null, null);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);

    }

    private DecodedJWT verifyToken(String token){
        return jwtVerifier.verify(token);


    }
}
