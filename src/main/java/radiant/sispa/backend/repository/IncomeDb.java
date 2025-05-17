package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Income;
import radiant.sispa.backend.model.Transaction;

@Repository
public interface IncomeDb extends JpaRepository<Income, Long> {

}
