package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Transaction;
import radiant.sispa.backend.model.TransactionCategory;

import java.util.Optional;

@Repository
public interface TransactionDb extends JpaRepository<Transaction, Long> {

}
