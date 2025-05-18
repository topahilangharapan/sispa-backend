package radiant.sispa.backend.restservice;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Transaction;
import radiant.sispa.backend.repository.ExpenseDb;
import radiant.sispa.backend.repository.IncomeDb;
import radiant.sispa.backend.restdto.response.TransactionResponseDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.util.Optional;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    IncomeDb incomeDb;

    @Autowired
    ExpenseDb expenseDb;
    
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public TransactionResponseDTO getTransactionById(String id) {
        Optional<? extends Transaction> transactionOpt = incomeDb.findById(id);

        if (transactionOpt.isEmpty()) {
            transactionOpt = expenseDb.findById(id);
        }

        if (transactionOpt.isEmpty()) {
            return null;
        }

        Transaction transaction = transactionOpt.get();

        if (transaction.getDeletedAt() != null) {
            return null;
        }

        return transactionToTransactionResponseDTO(transaction);
    }


    private TransactionResponseDTO transactionToTransactionResponseDTO(Transaction transaction) {
        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();

        transactionResponseDTO.setId(transaction.getId());
        transactionResponseDTO.setAmount(transaction.getAmount());
        transactionResponseDTO.setDescription(transaction.getDescription());
        transactionResponseDTO.setCreatedBy(transaction.getCreatedBy());
        transactionResponseDTO.setCreatedAt(transaction.getCreatedAt());
        transactionResponseDTO.setUpdatedBy(transaction.getUpdatedBy());
        transactionResponseDTO.setUpdatedAt(transaction.getUpdatedAt());
        transactionResponseDTO.setAccount(transaction.getAccount());
        transactionResponseDTO.setCategory(transaction.getCategory());
        return transactionResponseDTO;
    }
}
