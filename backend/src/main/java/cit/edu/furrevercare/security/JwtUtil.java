package cit.edu.furrevercare.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
// Remove: import io.jsonwebtoken.SignatureAlgorithm; // No longer generating key this way
import io.jsonwebtoken.io.Decoders; // Needed for Base64 decoding
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; // Import Value
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey; // Use specific SecretKey type
// Remove: import java.security.Key; // Use SecretKey instead
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final SecretKey key; // Use SecretKey type for HMAC
    private final long expiration = 1000 * 60 * 60 * 10; // 10 hours

    // Inject the secret from application.properties / environment variable
    public JwtUtil(@Value("${jwt.secret}") String secretString) {
        // Decode the Base64 secret string provided in the config
        byte[] keyBytes = Decoders.BASE64.decode(secretString);
        // Create the Key object from the decoded bytes
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate token using the injected key
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key) // Now uses the consistent key
                .compact();
    }

    // --- Extract methods remain the same, ensure they use the 'key' field ---

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Use the injected key for parsing/validation
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // Now uses the consistent key
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        try {
             return extractExpiration(token).before(new Date());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // If the library itself says it's expired during parsing
            return true;
        }
    }


    public Boolean validateToken(String token, String userId) {
         try {
            final String extractedUserId = extractUserId(token);
            // Check if user ID matches AND token is not expired (redundant check if extractUserId throws ExpiredJwtException)
             return (extractedUserId.equals(userId) && !isTokenExpired(token));
         } catch (io.jsonwebtoken.JwtException e) {
             // Catch any JWT parsing/validation errors (expired, malformed, wrong signature)
             logger.error("JWT validation error: {}", e.getMessage());
             return false;
         }
     }
     // Add logger if not present
     private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtUtil.class);
}