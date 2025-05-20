package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Account;
import radiant.sispa.backend.model.Expense;
import radiant.sispa.backend.model.Income;
import radiant.sispa.backend.model.TransactionCategory;
import radiant.sispa.backend.repository.ExpenseDb;
import radiant.sispa.backend.repository.IncomeDb;
import radiant.sispa.backend.restdto.request.CreateExpenseRequestDTO;
import radiant.sispa.backend.restdto.request.CreateIncomeRequestDTO;
import radiant.sispa.backend.restdto.response.CreateExpenseResponseDTO;
import radiant.sispa.backend.restdto.response.CreateIncomeResponseDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    private ExpenseDb expenseDb;

    @Autowired
    private IncomeDb incomeDb;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionCategoryService transactionCategoryService;

    @Override
    public CreateExpenseResponseDTO addExpense(CreateExpenseRequestDTO requestDTO, String authHeader) {

        String token = authHeader.substring(7);
        String createdBy = jwtUtils.getUserNameFromJwtToken(token);

        Account account = accountService.getAccountByNo(requestDTO.getAccount());
        if (account == null) {
            throw new EntityNotFoundException(String.format("Akun Bank dengan no %s tidak ditemukan.", requestDTO.getAccount()));
        } else if (accountService.getTotalBalance(account) < requestDTO.getAmount()) {
            throw new IllegalStateException(
                    String.format("Saldo akun bank dengan no %s tidak mencukupi", requestDTO.getAccount())
            );
        }

        TransactionCategory transactionCategory = transactionCategoryService.getTransactionCategoryByName(requestDTO.getCategory());
        if (transactionCategory == null) {
            throw new EntityNotFoundException(String.format("Kategori transaksi %s tidak ditemukan.", requestDTO.getCategory()));
        }

        Expense newExpense = new Expense();

        newExpense.setId(generateExpenseId(account));
        newExpense.setAmount(requestDTO.getAmount() * -1);
        newExpense.setCreatedBy(createdBy);
        newExpense.setCategory(transactionCategory);
        newExpense.setAccount(account);
        newExpense.setAdmin(requestDTO.isAdmin());
        newExpense.setDescription(requestDTO.getDescription());

        Expense expense = expenseDb.save(newExpense);

        CreateExpenseResponseDTO createExpenseResponseDTO = new CreateExpenseResponseDTO();

        createExpenseResponseDTO.setAmount(expense.getAmount() * -1);
        createExpenseResponseDTO.setAdmin(expense.isAdmin());
        createExpenseResponseDTO.setDescription(expense.getDescription());
        createExpenseResponseDTO.setAccount(expense.getAccount().getNo());
        createExpenseResponseDTO.setCategory(expense.getCategory().getName());

        return createExpenseResponseDTO;
    }

    public String generateExpenseId(Account account) {
        String last4Account = account.getNo().length() >= 4 ?
                account.getNo().substring(account.getNo().length() - 4) : account.getNo();

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        LocalDate todayDate = LocalDate.now();
        Instant startOfDay = todayDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = todayDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        long countToday = expenseDb.countExpenseToday(startOfDay, endOfDay);

        long newCount = countToday + 1;

        String formattedCount = String.format("%04d", newCount);

        return String.format("E/%s/%s/%s/%s", last4Account, account.getBank().getName(), today, formattedCount);
    }

    @Override
    public List<CreateExpenseResponseDTO> getExpensesByAccount(Long accountId) {
//        List<Expense> expenses = expenseDb.findByAccountId(accountId);
        List<CreateExpenseResponseDTO> responseList = new ArrayList<>();
        for (Expense expense : expenses) {
            CreateExpenseResponseDTO dto = new CreateExpenseResponseDTO();
            dto.setAmount(expense.getAmount());
            dto.setAdmin(expense.isAdmin());
            dto.setDescription(expense.getDescription());
            dto.setAccount(expense.getAccount().getNo());
            dto.setCategory(expense.getCategory().getName());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").withZone(ZoneId.of("Asia/Jakarta"));
            dto.setCreatedAt(formatter.format(expense.getCreatedAt()));
            // Add other fields as needed
            responseList.add(dto);
        }
        return responseList;
    }
}
