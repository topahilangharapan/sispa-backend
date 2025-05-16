package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Account;
import radiant.sispa.backend.model.Bank;

import java.util.Optional;

@Repository
public interface AccountDb extends JpaRepository<Account, Long> {
    Optional<Account> findByNo(String no);
}
