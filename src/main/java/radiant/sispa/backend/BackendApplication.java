package radiant.sispa.backend;

import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import radiant.sispa.backend.model.Role;
import radiant.sispa.backend.model.UserModel;
import radiant.sispa.backend.repository.RoleDb;
import radiant.sispa.backend.repository.UserDb;
import radiant.sispa.backend.restservice.UserRestService;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.capitalize;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	@Transactional
	CommandLineRunner run(RoleDb roleDb, UserDb userDb, UserRestService userService) {
		return args -> {
			if (roleDb.findByRole("ADMIN").orElse(null) == null) {
				Role role = new Role();
				role.setRole("ADMIN");
				role.setCreatedBy("hilangharapan");
				roleDb.save(role);
			}
			UserModel user;

			if (userDb.findByUsername("admin") == null) {
				user = new UserModel();
				user.setEmail("admin@gmail.com");
				user.setName("Admin");
				user.setUsername("admin");
				user.setPassword(userService.hashPassword("admin"));
				user.setRole(roleDb.findByRole("ADMIN").orElse(null));
				user.setCreatedBy("hilangharapan");
				userDb.save(user);
			}

			var faker = new Faker(new Locale("in-ID"));
		};
	}
}
