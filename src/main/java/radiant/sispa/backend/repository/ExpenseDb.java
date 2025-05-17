package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Expense;
import radiant.sispa.backend.model.Income;

@Repository
public interface ExpenseDb extends JpaRepository<Expense, Long> {

}
