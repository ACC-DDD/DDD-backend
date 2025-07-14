package acc.firewatch.config.jwt;

import acc.firewatch.common.exception.CustomException;
import acc.firewatch.common.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Configuration
@Getter
public class JwtTokenProvider {

    @Value("${jwt.secret.key}")
    private String secret;
    private Key key;
    @Value("${jwt.secret.expiration.access-token}")
    private long accessExpiration;
    @Value("${jwt.secret.expiration.refresh-token}")
    private long refreshExpiration;

    @PostConstruct
    public void init() {
        if(this.secret == null || secret.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_JWT_SECRET);
        }
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    public String generateToken(String phoneNum, Long memberId) {
        return createToken(phoneNum, memberId, accessExpiration);
    }

    public String generateRefreshToken(String phoneNum, Long memberId) {
        return createToken(phoneNum, memberId, refreshExpiration);
    }

    private String createToken(String phoneNum, Long memberId, long expiration) {
        Claims claims = Jwts.claims().setSubject(phoneNum);
        claims.put("memberId", memberId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.JWT_EXPIRED);
        } catch (MalformedJwtException e) {
            throw new CustomException(ErrorCode.JWT_PARSING_FAILED);
        } catch (SignatureException e) {
            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.JWT_PARSING_FAILED);
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
