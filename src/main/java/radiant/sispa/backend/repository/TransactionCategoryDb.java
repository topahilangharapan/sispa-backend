package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Account;
import radiant.sispa.backend.model.TransactionCategory;

import java.util.Optional;

@Repository
public interface TransactionCategoryDb extends JpaRepository<TransactionCategory, Long> {
    Optional<TransactionCategory> findByName(String name);
}
