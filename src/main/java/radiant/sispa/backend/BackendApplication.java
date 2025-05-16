package radiant.sispa.backend;

import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import radiant.sispa.backend.model.*;
import radiant.sispa.backend.repository.*;
import radiant.sispa.backend.restservice.UserRestService;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    @Transactional
    CommandLineRunner run(RoleDb roleDb, UserDb userDb, UserRestService userService, VendorDb vendorDb, ClientDb clientDb, WorkExperienceCategoryDb workExperienceCategoryDb, CategoryDb categoryDb, ItemStatusDb itemStatusDb, EducationLevelDb educationLevelDb) {
        return args -> {
            // Create roles if they don't exist
            createRoleIfNotExists(roleDb, "ADMIN");
            createRoleIfNotExists(roleDb, "MANAJEMEN");
            createRoleIfNotExists(roleDb, "FINANCE");
            createRoleIfNotExists(roleDb, "MARKETING");
            createRoleIfNotExists(roleDb, "PURCHASING");
            createRoleIfNotExists(roleDb, "FREELANCER");

            createUserIfNotExists(userDb, userService, roleDb, "admin", "admin@gmail.com", "Admin", "ADMIN");
            createUserIfNotExists(userDb, userService, roleDb, "manajemen", "manajemen@gmail.com", "Manajemen", "MANAJEMEN");
            createUserIfNotExists(userDb, userService, roleDb, "finance", "finance@gmail.com", "Finance", "FINANCE");
            createUserIfNotExists(userDb, userService, roleDb, "marketing", "marketing@gmail.com", "Marketing", "MARKETING");
            createUserIfNotExists(userDb, userService, roleDb, "purchasing", "purchasing@gmail.com", "Purchasing", "PURCHASING");

            for (int i = 0; i < 5; i++) {
                createRandomVendor(vendorDb);
                createRandomClient(clientDb);
            }

            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "ASISTEN MATA KULIAH");
            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "BOOTCAMP");
            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "FREELANCE");
            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "FULLTIME");
            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "KEGIATAN KAMPUS");
            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "KONTRAK");
            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "LOMBA");
            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "MAGANG");
            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "ORGANISASI");
            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "PARUH WAKTU");
            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "PENGABDIAN MASYARAKAT");
            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "PENGALAMAN MENGAJAR");
            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "PROYEK SUKARELA");
            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "RISET");
            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "TRAINING");
            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "WIRAUSAHA");
            createWorkExperienceCategoryIfNotExists(workExperienceCategoryDb, "LAINNYA");

            createCategoryIfNotExists(categoryDb, "MAINAN");
            createCategoryIfNotExists(categoryDb, "FURNITUR");
            createCategoryIfNotExists(categoryDb, "MISTIS");
            createCategoryIfNotExists(categoryDb, "MAKANAN");

            createItemStatusIfNotExists(itemStatusDb, "TERSEDIA");
            createItemStatusIfNotExists(itemStatusDb, "TIDAK TERSEDIA");
            createItemStatusIfNotExists(itemStatusDb, "DIPERBAIKI");

            createEducationLevelIfNotExists(educationLevelDb, "TK");
            createEducationLevelIfNotExists(educationLevelDb, "SD");
            createEducationLevelIfNotExists(educationLevelDb, "SMP");
            createEducationLevelIfNotExists(educationLevelDb, "SMA");
            createEducationLevelIfNotExists(educationLevelDb, "SMK");
            createEducationLevelIfNotExists(educationLevelDb, "D1");
            createEducationLevelIfNotExists(educationLevelDb, "D2");
            createEducationLevelIfNotExists(educationLevelDb, "D3");
            createEducationLevelIfNotExists(educationLevelDb, "D4");
            createEducationLevelIfNotExists(educationLevelDb, "S1");
            createEducationLevelIfNotExists(educationLevelDb, "S2");
            createEducationLevelIfNotExists(educationLevelDb, "S3");
            createEducationLevelIfNotExists(educationLevelDb, "LAINNYA");
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

    private void createRandomVendor(VendorDb vendorDb) {
        var faker = new Faker(new Locale("in-ID"));

        Vendor newVendor = new Vendor();

        newVendor.setName(trimToMaxLength(faker.company().name(), 255));
        newVendor.setEmail(trimToMaxLength(faker.name().firstName(), 245) + "@gmail.com");
        newVendor.setAddress(trimToMaxLength(faker.address().streetAddress(), 255));
        newVendor.setContact(trimToMaxLength(faker.phoneNumber().phoneNumber(), 50));
        newVendor.setService(trimToMaxLength(faker.job().field(), 255));
        newVendor.setDescription(trimToMaxLength(faker.yoda().quote(), 255));
        newVendor.setCreatedAt(new Date());
        newVendor.setUpdatedAt(new Date());
        newVendor.setUpdatedBy("hilangharapan");
        newVendor.setCreatedBy("hilangharapan");
        newVendor.setId(UUID.randomUUID().toString() + "_JAVA_FAKER");

        vendorDb.save(newVendor);
    }

    private void createRandomClient(ClientDb clientDb) {
        var faker = new Faker(new Locale("in-ID"));

        Client newClient = new Client();

        newClient.setName(trimToMaxLength(faker.company().name(), 255));
        newClient.setEmail(trimToMaxLength(faker.name().firstName(), 245) + "@gmail.com");
        newClient.setAddress(trimToMaxLength(faker.address().streetAddress(), 255));
        newClient.setContact(trimToMaxLength(faker.phoneNumber().phoneNumber(), 50));
        newClient.setIndustry(trimToMaxLength(faker.job().field(), 255));
        newClient.setDescription(trimToMaxLength(faker.yoda().quote(), 255));
        newClient.setCreatedAt(new Date());
        newClient.setUpdatedAt(new Date());
        newClient.setUpdatedBy("hilangharapan");
        newClient.setCreatedBy("hilangharapan");
        newClient.setId(UUID.randomUUID().toString() + "_JAVA_FAKER");

        clientDb.save(newClient);
    }

    private static String trimToMaxLength(String input, int maxLength) {
        return (input != null && input.length() > maxLength) ? input.substring(0, maxLength) : input;
    }

    private void createWorkExperienceCategoryIfNotExists(WorkExperienceCategoryDb workExperienceCategoryDb, String categoryName) {
        if (workExperienceCategoryDb.findByCategory(categoryName).orElse(null) == null) {
            WorkExperienceCategory workExperienceCategory = new WorkExperienceCategory();
            workExperienceCategory.setCategory(categoryName);
            workExperienceCategory.setCreatedBy("hilangharapan");
            workExperienceCategoryDb.save(workExperienceCategory);
        }
    }


    private void createCategoryIfNotExists(CategoryDb categoryDb, String categoryName) {
        ItemCategory newCategory = new ItemCategory();

        newCategory.setName(categoryName);
        newCategory.setCreatedBy("hilangharapan");

        categoryDb.save(newCategory);
    }

    private void createItemStatusIfNotExists(ItemStatusDb itemStatusDb, String itemStatusName) {
        ItemStatus newItemStatus = new ItemStatus();

        newItemStatus.setStatus(itemStatusName);
        newItemStatus.setCreatedBy("hilangharapan");

        itemStatusDb.save(newItemStatus);
    }

    private void createEducationLevelIfNotExists(EducationLevelDb educationLevelDb, String educationLevelName) {
        EducationLevel newEducationLevel = new EducationLevel();

        newEducationLevel.setEducation(educationLevelName);
        newEducationLevel.setCreatedBy("hilangharapan");

        educationLevelDb.save(newEducationLevel);
    }
}
