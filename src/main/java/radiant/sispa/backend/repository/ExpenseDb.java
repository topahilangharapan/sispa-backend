package radiant.sispa.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Account;
import radiant.sispa.backend.model.Expense;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface ExpenseDb extends JpaRepository<Expense, String> {

    @Query("SELECT COUNT(e) FROM Expense e WHERE e.createdAt >= :startOfDay AND e.createdAt < :endOfDay")
    long countExpenseToday(@Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay);

    ArrayList<Expense> findByDeletedAtIsNull();
    Optional<Expense> findTopByAccountAndDeletedAtIsNullOrderByCreatedAtDesc(Account account);
    List<Expense> findByAccountIdAndDeletedAtIsNull(Long accountId);

    ArrayList<Expense> findByAccountAndDeletedAtIsNull(Account account);
    ArrayList<Expense> findAllByDeletedAtIsNull();
}
