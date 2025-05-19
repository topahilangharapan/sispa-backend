package radiant.sispa.backend.restservice;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import radiant.sispa.backend.model.Expense;
import radiant.sispa.backend.model.Income;
import radiant.sispa.backend.model.Transaction;
import radiant.sispa.backend.repository.ExpenseDb;
import radiant.sispa.backend.repository.IncomeDb;
import radiant.sispa.backend.restdto.response.TransactionResponseDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;
import radiant.sispa.backend.restdto.response.BankBalanceDTO;

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

    @Override
    public List<BankBalanceDTO> getBalancePerBank() {
        List<Income> incomes = incomeDb.findByDeletedAtIsNull();
        List<Expense> expenses = expenseDb.findByDeletedAtIsNull();

        Map<String, Double> balanceMap = new HashMap<>();

        // Hitung income per bank
        for (Income income : incomes) {
            String bankName = income.getAccount().getBank().getName();
            balanceMap.put(bankName,
                    balanceMap.getOrDefault(bankName, 0.0) + income.getAmount());
        }

        // Kurangi expense per bank
        for (Expense expense : expenses) {
            String bankName = expense.getAccount().getBank().getName();
            balanceMap.put(bankName,
                    balanceMap.getOrDefault(bankName, 0.0) - expense.getAmount());
        }

        List<BankBalanceDTO> result = new ArrayList<>();
        for (var entry : balanceMap.entrySet()) {
            result.add(new BankBalanceDTO(entry.getKey(), entry.getValue()));
        }

        return result;
    }

    @Override
    public void deleteTransaction(String id, String authHeader) throws EntityNotFoundException {
        String token = authHeader.substring(7);
        String deletedBy = jwtUtils.getUserNameFromJwtToken(token);

        Optional<? extends Transaction> transactionToDelete = incomeDb.findById(id);
        
        if (transactionToDelete.isEmpty()) {
            transactionToDelete = expenseDb.findById(id);
        }
        
        if (transactionToDelete.isEmpty()) {
            throw new EntityNotFoundException("Transaksi tidak ditemukan");
        }
        
        Transaction transaction = transactionToDelete.get();
        
        if (transaction.getDeletedAt() != null) {
            return;
        }
        
        transaction.setDeletedAt(new Date().toInstant());
        transaction.setDeletedBy(deletedBy);
        
        if (transaction instanceof Income) {
            incomeDb.save((Income) transaction);
        } else if (transaction instanceof Expense) {
            expenseDb.save((Expense) transaction);
        }
    }
    
}
