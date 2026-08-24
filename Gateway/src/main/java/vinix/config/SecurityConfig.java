package vinix.config;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAutenticacao jwtAutenticacao;

  private static final String[] ENDPOINTS_PUBLICOS = {
      "/categorias/**", "/notificacoes/**",
      "/pedidos/**", "/clientes/**",
      "/produtos/**"
  };

  @Value("${jwt.secret}")
  private String jwtSecreta;

  @Bean
  SecretKey secretKey() {
    return Keys.hmacShaKeyFor(jwtSecreta.getBytes(StandardCharsets.UTF_8));
  }

  @Bean
  SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http.csrf(csfr -> csfr.disable())
        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        .authorizeExchange(exchange -> exchange
            .pathMatchers(ENDPOINTS_PUBLICOS).permitAll()
            .anyExchange().authenticated())
        .addFilterAt(jwtAutenticacao, SecurityWebFiltersOrder.AUTHENTICATION)
        .build();
  }
}
