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

import java.util.Locale;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    @Transactional
    CommandLineRunner run(RoleDb roleDb, UserDb userDb, UserRestService userService) {
        return args -> {
            // Create roles if they don't exist
            createRoleIfNotExists(roleDb, "ADMIN");
            createRoleIfNotExists(roleDb, "MANAJEMEN");
            createRoleIfNotExists(roleDb, "FINANCE");
            createRoleIfNotExists(roleDb, "MARKETING");
            createRoleIfNotExists(roleDb, "PURCHASING");

            // Create users if they don't exist
            createUserIfNotExists(userDb, userService, roleDb, "admin", "admin@gmail.com", "Admin", "ADMIN");
            createUserIfNotExists(userDb, userService, roleDb, "manajemen", "manajemen@gmail.com", "Manajemen", "MANAJEMEN");
            createUserIfNotExists(userDb, userService, roleDb, "finance", "finance@gmail.com", "Finance", "FINANCE");
            createUserIfNotExists(userDb, userService, roleDb, "marketing", "marketing@gmail.com", "Marketing", "MARKETING");
            createUserIfNotExists(userDb, userService, roleDb, "purchasing", "purchasing@gmail.com", "Purchasing", "PURCHASING");

            // Faker example, if you need to use it later for test data
            var faker = new Faker(new Locale("in-ID"));
        };
    }

    private void createRoleIfNotExists(RoleDb roleDb, String roleName) {
        if (roleDb.findByRole(roleName).orElse(null) == null) {
            Role role = new Role();
            role.setRole(roleName);
            role.setCreatedBy("hilangharapan");
            roleDb.save(role);
        }
    }

    private void createUserIfNotExists(UserDb userDb, UserRestService userService, RoleDb roleDb, 
                                       String username, String email, String name, String roleName) {
        if (userDb.findByUsername(username) == null) {
            UserModel user = new UserModel();
            user.setEmail(email);
            user.setName(name);
            user.setUsername(username);
            user.setPassword(userService.hashPassword(username)); // Default password is the username, can be changed later
            user.setRole(roleDb.findByRole(roleName).orElse(null));
            user.setCreatedBy("hilangharapan");
            userDb.save(user);
        }
    }
}
