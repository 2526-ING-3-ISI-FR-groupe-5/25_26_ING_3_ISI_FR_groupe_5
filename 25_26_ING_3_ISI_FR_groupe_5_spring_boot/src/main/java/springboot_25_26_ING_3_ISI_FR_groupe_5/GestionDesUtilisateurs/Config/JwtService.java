package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Role;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.secret}")         // ✅ lu depuis application.properties
    private String SECRET_KEY;

    @Value("${jwt.expiration}")     // ✅ lu depuis application.properties
    private Long JWT_EXPIRATION_TIME;

    public String generateJwtToken(Utilisateur utilisateurs) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", utilisateurs.getRoles()
                .stream()
                .map(Role::getNom)
                .collect(Collectors.toList()));

        claims.put("permissions", utilisateurs.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        claims.put("firstLogin", utilisateurs.isFirstLogin());

        return Jwts.builder()
                .claims(claims)
                .subject(utilisateurs.getEmail())   // ✅ email seul (plus simple à extraire)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME))
                .signWith(getSignKey())              // ✅ plus besoin de préciser l'algo
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())   // ✅ nouveau nom dans jjwt 0.12+
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // ✅ BASE64, pas BASE64URL
        return Keys.hmacShaKeyFor(keyBytes);                  // ✅ HMAC → HS256 automatique
    }
}