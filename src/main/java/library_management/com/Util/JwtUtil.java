package library_management.com.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static io.jsonwebtoken.io.Decoders.BASE64;

@Configuration
public class JwtUtil {

    @Value( "${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key SigningKey() {
        byte[] keyBytes = BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String subject, String role) {
        return createToken(subject, role);
    }

    private String createToken(String subject, String role) {
        return Jwts.builder()
                .claim("role", role)        // ekstra claim
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SigningKey())
                .compact();
    }
    // ELECTRE YOK NORMAL TOPSİS YOK PROMETHEE AHP VIKOR GELEBILIR Ekstra olarak bulanık yontemlerden bırı cıkıcak

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token , String subject) {
        final String extractSubject = extractSubject(token);
        return extractSubject.equals(subject) && !isTokenExpired(token);
    }
}
