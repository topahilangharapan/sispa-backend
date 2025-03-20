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
			createRole(roleDb, "ADMIN");
			createRole(roleDb, "MARKETING");
			createRole(roleDb, "FINANCE");
			createRole(roleDb, "PURCHASING");
			createRole(roleDb, "MANAGEMENT");

			createUser(userDb, userService, roleDb, "ADMIN");
			createUser(userDb, userService, roleDb, "MARKETING");
			createUser(userDb, userService, roleDb, "FINANCE");
			createUser(userDb, userService, roleDb, "PURCHASING");
			createUser(userDb, userService, roleDb, "MANAGEMENT");

			var faker = new Faker(new Locale("in-ID"));
		};
	}

	private void createRole(RoleDb roleDb, String roleName) {
		if (roleDb.findByRole(roleName).orElse(null) == null) {
			Role role = new Role();
			role.setRole(roleName);
			role.setCreatedBy("hilangharapan");
			roleDb.save(role);
		}
	}

	private void createUser(UserDb userDb, UserRestService userService, RoleDb roleDb, String role) {
		UserModel user;
		if (userDb.findByUsername(role) == null) {
			user = new UserModel();
			user.setEmail(role + "@gmail.com");
			user.setName(role);
			user.setUsername(role.toLowerCase());
			user.setPassword(userService.hashPassword(role.toLowerCase()));
			user.setRole(roleDb.findByRole(role).orElse(null));
			user.setCreatedBy("hilangharapan");
			userDb.save(user);
		}
	}
}
