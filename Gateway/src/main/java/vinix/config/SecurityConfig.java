package vinix.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAutenticacao jwtAutenticacao;

  private static final String[] ENDPOINTS_PUBLICOS = {
      "/categorias/**", "/notificacoes/**",
      "/pedidos/**", "/clientes/**",
      "/produtos/**", "/swagger-ui/**",
      "/swagger-ui.html", "/v3/api-docs/**"
  };

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
