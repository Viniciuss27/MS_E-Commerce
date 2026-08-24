package vinix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import vinix.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final UserDetailsServiceImpl detailsServiceImpl;
	
				private static final String[] ENDPOINTS_PUBLICOS = {
						      "/auth/login",
				        "/auth/register",
				        "/v3/api-docs/**",
				        "/swagger-ui/**",
				        "/docs"
				};

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean // Recebe email+senha → busca usuário → compara senha 
    DaoAuthenticationProvider authenticationProvider() {
    	DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    	provider.setUserDetailsService(detailsServiceImpl);//usuario
    	provider.setPasswordEncoder(passwordEncoder());//senha
    	return provider;
    }
    
    @Bean // Autentica
    AuthenticationManager authenticationManager(
    		AuthenticationConfiguration config) throws Exception {
    	return config.getAuthenticationManager();
    }
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http.csrf(csrf -> csrf.disable())
			    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			    .httpBasic(AbstractHttpConfigurer::disable)
			    .formLogin(AbstractHttpConfigurer::disable)
			    .authorizeHttpRequests(auth -> auth
					    .requestMatchers(ENDPOINTS_PUBLICOS).permitAll()
					    .anyRequest().authenticated());

    return http.build();
   }
    
}