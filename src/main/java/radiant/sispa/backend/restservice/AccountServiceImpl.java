package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Account;
import radiant.sispa.backend.model.Bank;
import radiant.sispa.backend.model.Expense;
import radiant.sispa.backend.model.Income;
import radiant.sispa.backend.repository.AccountDb;
import radiant.sispa.backend.repository.BankDb;
import radiant.sispa.backend.repository.ExpenseDb;
import radiant.sispa.backend.repository.IncomeDb;
import radiant.sispa.backend.restdto.request.CreateAccountRequestDTO;
import radiant.sispa.backend.restdto.response.AccountResponseDTO;
import radiant.sispa.backend.restdto.response.CreateAccountResponseDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDb accountDb;

    @Autowired
    private BankDb bankDb;

    @Autowired
    private IncomeDb incomeDb;

    @Autowired
    private ExpenseDb expenseDb;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public List<Account> getAllAccounts() {
        return accountDb.findAll();
    }

    @Override
    public CreateAccountResponseDTO addAccount(CreateAccountRequestDTO requestDTO, String authHeader) throws EntityExistsException, EntityNotFoundException {
        String token = authHeader.substring(7);
        String createdBy = jwtUtils.getUserNameFromJwtToken(token);

        Account account = new Account();
        Bank bank = bankDb.findByName(requestDTO.getBank()).orElse(null);

        if (accountDb.findByNo(requestDTO.getNo().toUpperCase()).orElse(null) != null) {
            throw new EntityExistsException(String.format("Account %s already exists", requestDTO.getNo()));
        }

        if (bank == null) {
            throw new EntityNotFoundException(String.format("Bank %s doesnt exists", requestDTO.getBank()));
        }

        account.setName(requestDTO.getName().toUpperCase());
        account.setBank(bank);
        account.setNo(requestDTO.getNo());
        account.setCreatedBy(createdBy);

        accountDb.save(account);

        CreateAccountResponseDTO responseDTO = new CreateAccountResponseDTO();
        responseDTO.setName(account.getName());
        responseDTO.setBank(account.getBank().getName());
        responseDTO.setNo(account.getNo());

        return responseDTO;
    }

    @Override
    public List<AccountResponseDTO> accountToAccountResponseDTO(List<Account> accounts) {
        List<AccountResponseDTO> accountResponseDTOS = new ArrayList<>();
        for (Account account : accounts) {
            AccountResponseDTO accountResponseDTO = new AccountResponseDTO();

            accountResponseDTO.setId(account.getId());
            accountResponseDTO.setName(account.getName());
            accountResponseDTO.setNo(account.getNo());
            accountResponseDTO.setBank(account.getBank().getName());
            accountResponseDTO.setAdminFee(account.getBank().getAdminFee());
            accountResponseDTO.setInterestRate(account.getBank().getInterestRate());
            accountResponseDTO.setBalance(getTotalBalance(account));

            accountResponseDTO.setCreatedBy(account.getCreatedBy());
            accountResponseDTO.setCreatedAt(account.getCreatedAt());
            accountResponseDTO.setUpdatedBy(account.getUpdatedBy());
            accountResponseDTO.setUpdatedAt(account.getUpdatedAt());
            accountResponseDTO.setDeletedBy(account.getDeletedBy());
            accountResponseDTO.setDeletedAt(account.getDeletedAt());

            accountResponseDTOS.add(accountResponseDTO);
        }
        return accountResponseDTOS;
    }

    @Override
    public Account getAccountByNo(String no) throws EntityNotFoundException {
        Account account = accountDb.findByNo(no.toUpperCase()).orElse(null);

        if(account == null) {
            throw new EntityNotFoundException(String.format("Account %s doesnt exist!", no));
        }

        return account;
    }

    @Override
    public double getTotalBalance(Account account) {
        ArrayList<Income> incomes = incomeDb.findByDeletedAtIsNull();
        ArrayList<Expense> expenses = expenseDb.findByDeletedAtIsNull();

        double totalBalance = 0;

        for (Income income : incomes) {
            totalBalance += income.getAmount();
        }

        for (Expense expense : expenses) {
            totalBalance += expense.getAmount();
        }

        return totalBalance;
    }
}
