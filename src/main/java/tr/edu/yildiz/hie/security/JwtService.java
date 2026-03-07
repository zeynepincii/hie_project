package tr.edu.yildiz.hie.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    // Kendi sistemimize özel, 256-bit (32 karakter) karmaşık bir gizli anahtar.
    // Gerçek projelerde bu kodun içinde değil, application.properties içinde tutulur.
    private static final String SECRET_KEY = "HizmeticiEgitimSistemiIcinCokGizliVeKarmaSikBirAnahtarKelimeYZ";

    // 1. Token'ın içinden kullanıcının e-postasını (username) çıkaran metod
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 2. Kullanıcı başarılı giriş yaptığında ona yeni bir Token üreten metod
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Token süresi: 24 Saat
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Token'ı dijital olarak imzalıyoruz
                .compact();
    }

    // 3. Gelen token'ın bizim sistemimize ait olup olmadığını ve süresinin dolup dolmadığını kontrol eder
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
