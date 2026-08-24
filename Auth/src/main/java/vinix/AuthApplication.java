package vinix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class AuthApplication /*implements CommandLineRunner*/ {

  public static void main(String[] args) {
    SpringApplication.run(AuthApplication.class, args);
  }

	/*@Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private String expiration;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("PASSWORD: " + (expiration != null && !expiration.isBlank()));;
		System.out.println("USERNAME: " + (secret != null && !secret.isBlank()));;

	}*/

}
