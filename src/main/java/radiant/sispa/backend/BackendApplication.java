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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    @Transactional
    CommandLineRunner run(RoleDb roleDb, UserDb userDb, UserRestService userService, VendorDb vendorDb, ClientDb clientDb, WorkExperienceCategoryDb workExperienceCategoryDb, ItemCategoryDb itemCategoryDb, ItemStatusDb itemStatusDb, EducationLevelDb educationLevelDb, BankDb bankDb, TransactionCategoryDb transactionCategoryDb, AccountDb accountDb, IncomeDb incomeDb) {
        return args -> {
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

            createItemCategoryIfNotExists(itemCategoryDb, "ELEKTRONIK");
            createItemCategoryIfNotExists(itemCategoryDb, "FURNITUR");
            createItemCategoryIfNotExists(itemCategoryDb, "PAKAIAN");
            createItemCategoryIfNotExists(itemCategoryDb, "MAKANAN");
            createItemCategoryIfNotExists(itemCategoryDb, "MINUMAN");
            createItemCategoryIfNotExists(itemCategoryDb, "ALAT TULIS");
            createItemCategoryIfNotExists(itemCategoryDb, "MAINAN");
            createItemCategoryIfNotExists(itemCategoryDb, "PERLENGKAPAN DAPUR");
            createItemCategoryIfNotExists(itemCategoryDb, "PERLENGKAPAN MANDI");
            createItemCategoryIfNotExists(itemCategoryDb, "OBAT-OBATAN");
            createItemCategoryIfNotExists(itemCategoryDb, "PERALATAN OLAHRAGA");
            createItemCategoryIfNotExists(itemCategoryDb, "BUKU");
            createItemCategoryIfNotExists(itemCategoryDb, "ALAT KEBERSIHAN");
            createItemCategoryIfNotExists(itemCategoryDb, "PERLENGKAPAN BAYI");
            createItemCategoryIfNotExists(itemCategoryDb, "PERLENGKAPAN HEWAN");
            createItemCategoryIfNotExists(itemCategoryDb, "ALAT MUSIK");
            createItemCategoryIfNotExists(itemCategoryDb, "AKSESORIS");
            createItemCategoryIfNotExists(itemCategoryDb, "KOSMETIK");
            createItemCategoryIfNotExists(itemCategoryDb, "PERKAKAS");
            createItemCategoryIfNotExists(itemCategoryDb, "PERLENGKAPAN KANTOR");

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

            createBankIfNotExists(bankDb, "MANDIRI", 0.2, 5000);
            createBankIfNotExists(bankDb, "BCA", 0.2, 10000);
            createBankIfNotExists(bankDb, "BSI", 0.125, 2500);

            createTransactionCategoryIfNotExists(transactionCategoryDb, "PENDAPATAN KLIEN");
            createTransactionCategoryIfNotExists(transactionCategoryDb, "BIAYA VENUE");
            createTransactionCategoryIfNotExists(transactionCategoryDb, "DEKORASI DAN SETTING");
            createTransactionCategoryIfNotExists(transactionCategoryDb, "TALENT DAN MC");
            createTransactionCategoryIfNotExists(transactionCategoryDb, "PERIZINAN DAN LEGALITAS");
            createTransactionCategoryIfNotExists(transactionCategoryDb, "KONSUMSI DAN KATERING");
            createTransactionCategoryIfNotExists(transactionCategoryDb, "TRANSPORTASI DAN AKOMODASI");
            createTransactionCategoryIfNotExists(transactionCategoryDb, "PROMOSI DAN MARKETING");
            createTransactionCategoryIfNotExists(transactionCategoryDb, "CETAK DAN MERCHANDISE");
            createTransactionCategoryIfNotExists(transactionCategoryDb, "GAJI DAN OPERASIONAL KARYAWAN");
            createTransactionCategoryIfNotExists(transactionCategoryDb, "BUNGA BANK");
            createTransactionCategoryIfNotExists(transactionCategoryDb, "ADMINISTRASI BANK");
            createTransactionCategoryIfNotExists(transactionCategoryDb, "LAIN-LAIN");

            createAccountIfNotExists(accountDb, "hilangharapan", bankDb.findByName("MANDIRI").orElse(null), "040504-MANDIRI");
            createAccountIfNotExists(accountDb, "hilangharapan", bankDb.findByName("BCA").orElse(null), "040504-BCA");

            Faker faker = new Faker();
            Random random = new Random();

            for (int i = 0; i < random.nextInt(100, 1000); i++) {
                if (i % 2 == 0) {
                    createIncomeAtRandom(incomeDb, accountDb.findByNo("040504-MANDIRI").orElse(null), transactionCategoryDb.findByName("LAIN-LAIN").orElse(null),faker, random);
                } else {
                    createIncomeAtRandom(incomeDb, accountDb.findByNo("040504-BCA").orElse(null), transactionCategoryDb.findByName("LAIN-LAIN").orElse(null),faker, random);
                }
            }
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


    private void createItemCategoryIfNotExists(ItemCategoryDb categoryDb, String categoryName) {
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

    private void createBankIfNotExists(BankDb bankDb, String name, double interestRate, double adminFee) {
        Bank bank = new Bank();

        bank.setName(name);
        bank.setInterestRate(interestRate);
        bank.setAdminFee(adminFee);
        bank.setCreatedBy("hilangharapan");

        bankDb.save(bank);
    }

    private void createTransactionCategoryIfNotExists(TransactionCategoryDb transactionCategoryDb, String name) {
        TransactionCategory transactionCategory = new TransactionCategory();

        transactionCategory.setName(name);
        transactionCategory.setCreatedBy("hilangharapan");

        transactionCategoryDb.save(transactionCategory);
    }

    private void createAccountIfNotExists(AccountDb accountDb, String name, Bank bank, String no) {
        Account account = new Account();

        account.setName(name);
        account.setNo(no);
        account.setBank(bank);
        account.setCreatedBy("hilangharapan");

        accountDb.save(account);
    }

    private void createIncomeAtRandom(IncomeDb incomeDb, Account account, TransactionCategory transactionCategory, Faker faker, Random random) {
        Income income = new Income();

        income.setId(UUID.randomUUID().toString() + "-FAKER");
        income.setCategory(transactionCategory);
        income.setAccount(account);
        income.setCreatedBy("hilangharapan");
        income.setAmount(random.nextDouble(1000, 1000000000));
        income.setInterest(false);
        incomeDb.save(income);

        Date randomDate = faker.date().between(
                Date.from(LocalDate.of(2004, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2024, 12, 31).atStartOfDay(ZoneId.systemDefault()).toInstant())
        );

        income.setCreatedAt(randomDate.toInstant());
        incomeDb.save(income);
    }
}
