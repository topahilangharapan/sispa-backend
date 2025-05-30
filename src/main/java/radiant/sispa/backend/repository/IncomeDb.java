package radiant.sispa.backend.repository;

import java.lang.reflect.Array;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Income;
import radiant.sispa.backend.model.Account;

import java.time.Instant;
import java.util.ArrayList;

@Repository
public interface IncomeDb extends JpaRepository<Income, String> {

    @Query("SELECT COUNT(i) FROM Income i WHERE i.createdAt >= :startOfDay AND i.createdAt < :endOfDay")
    long countIncomeToday(@Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay);

    ArrayList<Income> findByDeletedAtIsNull();

    Optional<Income> findTopByAccountAndDeletedAtIsNullOrderByCreatedAtDesc(Account account);

    ArrayList<Income> findByAccountAndDeletedAtIsNull(Account account);

    ArrayList<Income> findByAccountIdAndDeletedAtIsNullOrderByTransactionDateDesc(Long accountId);

    ArrayList<Income> findAllByDeletedAtIsNullOrderByTransactionDateDesc();
}
