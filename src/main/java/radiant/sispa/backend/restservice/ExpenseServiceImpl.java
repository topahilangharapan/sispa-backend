package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    private ExpenseDb expenseDb;

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
        }

        TransactionCategory transactionCategory = transactionCategoryService.getTransactionCategoryByName(requestDTO.getCategory());
        if (transactionCategory == null) {
            throw new EntityNotFoundException(String.format("Kategori transaksi %s tidak ditemukan.", requestDTO.getCategory()));
        }

        Expense newExpense = new Expense();

        newExpense.setAmount(requestDTO.getAmount());
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


}
