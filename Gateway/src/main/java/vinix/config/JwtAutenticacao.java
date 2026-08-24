package vinix.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.aot.hint.TypeReference.listOf;

@Component
@RequiredArgsConstructor
public class JwtAutenticacao implements WebFilter{

private final SecretKey secretKey;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

    String header = exchange.getRequest().getHeaders().getFirst("Authorization");

    if (header == null || !header.startsWith("Bearer ")) {
      return chain.filter(exchange); // continua sem autenticar
    }

    String token = header.substring(7);

    try {
      Claims claims = Jwts.parser().verifyWith(secretKey)
          .build().parseSignedClaims(token).getPayload();

      String subject = claims.getSubject();

      // espera ["ADMIN", "USER"]
      List<String> roles = claims.get("roles", List.class);

      List<SimpleGrantedAuthority> authorities = (roles != null ? roles : List.<String>of())
          .stream()
          .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
          .toList();

      var autenticado = new UsernamePasswordAuthenticationToken(subject, null, authorities);

      return chain.filter(exchange)
          .contextWrite(ReactiveSecurityContextHolder.withAuthentication(autenticado));

    } catch ( JwtException e) {
    // Token inválido/expirado/forjado -> devolve 401 e encerra
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    return exchange.getResponse().setComplete();
  }
  }
}
