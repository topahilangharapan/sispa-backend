package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Expense;
import radiant.sispa.backend.model.Income;

import java.time.Instant;

@Repository
public interface ExpenseDb extends JpaRepository<Expense, String> {

    @Query("SELECT COUNT(e) FROM Expense e WHERE e.createdAt >= :startOfDay AND e.createdAt < :endOfDay")
    long countExpenseToday(@Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay);


}
