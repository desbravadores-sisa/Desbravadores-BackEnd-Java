package school.sptech.APIDesbravadores.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import school.sptech.APIDesbravadores.dto.UsuarioDetalhesDto;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GerenciadorTokenJwt {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${jwt.validity}")
    private long jwtTokenValidity;

    public String generateToken(final Authentication authentication) {
        final String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        UsuarioDetalhesDto usuarioDetalhes = (UsuarioDetalhesDto) authentication.getPrincipal();

        return Jwts.builder()
                .subject(authentication.getName())
                .claim("authorities", authorities)
                .claim("idClube", usuarioDetalhes.getIdClube())
                .claim("idUnidade", usuarioDetalhes.getIdUnidade())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtTokenValidity * 1_000))
                .signWith(parseSecret())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimForToken(token, Claims::getSubject);
    }

    public Integer getIdClubeFromToken(String token){
        return getClaimForToken(token, claims -> claims.get("idClube", Integer.class));
    }

    public Integer getIdUnidadeFromToken(String token){
        return getClaimForToken(token, claims -> claims.get("idUnidade", Integer.class));
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimForToken(token, Claims::getExpiration);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public <T> T getClaimForToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date(System.currentTimeMillis()));
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(parseSecret())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey parseSecret() {
        byte[] secretBytes;

        try {
            byte[] decodedSecret = Decoders.BASE64.decode(this.secret);
            secretBytes = decodedSecret.length >= 32
                    ? decodedSecret
                    : this.secret.getBytes(StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            secretBytes = this.secret.getBytes(StandardCharsets.UTF_8);
        }

        return Keys.hmacShaKeyFor(secretBytes);
    }
}
