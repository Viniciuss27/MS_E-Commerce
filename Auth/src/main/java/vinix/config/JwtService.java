package vinix.config;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

		@Value("${jwt.secret}")
		private String secret;

		@Value("${jwt.expiration}")
		private Long expiration;

	public String generateToken(String email, List<String> roles) {
		SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		Instant now = Instant.now();

		return Jwts.builder()
				.subject(email)
				.claim("roles", roles)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(expiration, ChronoUnit.MILLIS)))
				.signWith(key)
				.compact();
	}

		public Long getExpiration() {
			return expiration;
		}
}