package ningenaki.inc.termonal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TermonalApplication {

	public static void main(String[] args) {
		SpringApplication.run(TermonalApplication.class, args);
	}

}
