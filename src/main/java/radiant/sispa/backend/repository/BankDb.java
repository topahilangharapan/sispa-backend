package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Bank;
import radiant.sispa.backend.model.ItemCategory;
import radiant.sispa.backend.model.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankDb extends JpaRepository<Bank, Long> {
    Optional<Bank> findByName(String name);
}
