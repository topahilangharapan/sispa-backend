package radiant.sispa.backend.restservice;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import radiant.sispa.backend.model.Account;
import radiant.sispa.backend.model.Expense;
import radiant.sispa.backend.model.Income;
import radiant.sispa.backend.model.Transaction;
import radiant.sispa.backend.repository.AccountDb;
import radiant.sispa.backend.repository.ExpenseDb;
import radiant.sispa.backend.repository.IncomeDb;
import radiant.sispa.backend.restdto.request.CashFlowChartRequestDTO;
import radiant.sispa.backend.restdto.response.CashFlowChartResponseDTO;
import radiant.sispa.backend.restdto.response.TransactionResponseDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;
import radiant.sispa.backend.restdto.response.BankBalanceDTO;

import java.util.Optional;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    IncomeDb incomeDb;

    @Autowired
    ExpenseDb expenseDb;

    @Autowired
    AccountDb accountDb;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    AccountService accountService;

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
        transactionResponseDTO.setTransactionDate(transaction.getTransactionDate().toString());
        transactionResponseDTO.setCreatedBy(transaction.getCreatedBy());
        transactionResponseDTO.setCreatedAt(transaction.getCreatedAt());
        transactionResponseDTO.setUpdatedBy(transaction.getUpdatedBy());
        transactionResponseDTO.setUpdatedAt(transaction.getUpdatedAt());
        transactionResponseDTO.setAccount(accountService.accountToAccountResponseDTO(transaction.getAccount()));
        transactionResponseDTO.setCategory(transaction.getCategory());
        return transactionResponseDTO;
    }

    @Override
    public List<BankBalanceDTO> getBalancePerBank() {
        List<Income> incomes = incomeDb.findByDeletedAtIsNull();
        List<Expense> expenses = expenseDb.findByDeletedAtIsNull();

        Map<String, Double> balanceMap = new HashMap<>();
        Map<String, String> accountNumberMap = new HashMap<>();
        Map<String, Long> accountIdMap = new HashMap<>();

        // Hitung income per bank
        for (Income income : incomes) {
            String bankName = income.getAccount().getBank().getName();
            String accountNumber = income.getAccount().getNo();
            Long accountId = income.getAccount().getId();
            balanceMap.put(bankName,
                    balanceMap.getOrDefault(bankName, 0.0) + income.getAmount());
            accountNumberMap.putIfAbsent(bankName, accountNumber);
            accountIdMap.putIfAbsent(bankName, accountId);
        }

        // Kurangi expense per bank
        for (Expense expense : expenses) {
            String bankName = expense.getAccount().getBank().getName();
            String accountNumber = expense.getAccount().getNo();
            Long accountId = expense.getAccount().getId();
            balanceMap.put(bankName,
                    balanceMap.getOrDefault(bankName, 0.0) + expense.getAmount());
            accountNumberMap.putIfAbsent(bankName, accountNumber);
            accountIdMap.putIfAbsent(bankName, accountId);
        }

        List<BankBalanceDTO> result = new ArrayList<>();
        for (var entry : balanceMap.entrySet()) {
            String bankName = entry.getKey();
            double totalBalance = entry.getValue();
            String accountNumber = accountNumberMap.getOrDefault(bankName, "-");
            Long accountId = accountIdMap.getOrDefault(bankName, null);
            result.add(new BankBalanceDTO(bankName, totalBalance, accountNumber, accountId));
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

    @Override
    public ArrayList<CashFlowChartResponseDTO> getCashFlowChartData(CashFlowChartRequestDTO requestDTO) {
        Account account = accountDb.findByNo(requestDTO.getAccountNo()).orElse(null);

        if (account == null) {
            throw new EntityNotFoundException(String.format("Account dengan no %s tidak ditemukan", requestDTO.getAccountNo()));
        }

        List<Income> incomes = account.getIncomes();
        List<Expense> expenses = account.getExpenses();

        ArrayList<CashFlowChartResponseDTO> responseDTOS = new ArrayList<>();

        if (requestDTO.getType().equals("income")) {
            for (Income income : incomes) {
                CashFlowChartResponseDTO responseDTO = new CashFlowChartResponseDTO();

                LocalDateTime dateTime = income.getTransactionDate().atStartOfDay();

                int year = dateTime.getYear();
                int month = dateTime.getMonthValue();
                int quarter = (dateTime.getMonthValue() - 1) / 3 + 1;

                responseDTO.setAmount(income.getAmount());
                responseDTO.setBank(income.getAccount().getBank().getName());
                responseDTO.setYear(year);
                responseDTO.setMonth(month);
                responseDTO.setQuartal(quarter);

                responseDTOS.add(responseDTO);
            }
        } else {
            for (Expense expense : expenses) {
                CashFlowChartResponseDTO responseDTO = new CashFlowChartResponseDTO();

                LocalDateTime dateTime = expense.getTransactionDate().atStartOfDay();

                int year = dateTime.getYear();
                int month = dateTime.getMonthValue();
                int quarter = (dateTime.getMonthValue() - 1) / 3 + 1;

                responseDTO.setAmount(expense.getAmount() * -1);
                responseDTO.setBank(expense.getAccount().getBank().getName());
                responseDTO.setYear(year);
                responseDTO.setMonth(month);
                responseDTO.setQuartal(quarter);

                responseDTOS.add(responseDTO);
            }
        }

        return responseDTOS;
    }
}
