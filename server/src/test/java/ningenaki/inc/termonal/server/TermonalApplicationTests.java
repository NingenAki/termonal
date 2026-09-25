package ningenaki.inc.termonal.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"termonal.runner.enabled=false",
		"spring.autoconfigure.exclude="
				+ "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
				+ "org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration,"
				+ "org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration,"
				+ "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
				+ "org.springframework.boot.data.jdbc.autoconfigure.DataJdbcRepositoriesAutoConfiguration,"
				+ "org.springframework.boot.session.jdbc.autoconfigure.JdbcSessionAutoConfiguration"
})
class TermonalApplicationTests {

	@Test
	void contextLoads() {
	}

}
