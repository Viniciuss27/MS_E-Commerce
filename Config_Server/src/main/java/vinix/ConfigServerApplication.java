package vinix;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication /*implements CommandLineRunner*/ {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

    /* testado .env e repositorio git remoto
	@Value("${GIT_PASSWORD}")
	private String password;

	@Value("${GIT_USERNAME}")
	private String username;

    @Value("{jwt.expiration}")
    private String expiration;

    @Value("{jwt.secret}")
    private String secret;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("PASSWORD: " + (password != null && !password.isBlank()));;
		System.out.println("USERNAME: " + (username != null && !username.isBlank()));;
        System.out.println("EXPIRATION: " + (expiration != null && !expiration.isBlank()));
        System.out.println("SECRET: " + (secret != null && !secret.isBlank()));
	}*/
}
