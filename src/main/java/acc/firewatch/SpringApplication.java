package acc.firewatch;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringApplication {

	public static void main(String[] args) {
		org.springframework.boot.SpringApplication.run(SpringApplication.class, args);
	}

	@Value("${jwt.secret.expiration.access-token}")
	private long accessExpiration;

	@PostConstruct
	public void test() {
		System.out.println(">>> " + accessExpiration);
	}

}