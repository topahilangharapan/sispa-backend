package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import radiant.sispa.backend.model.Account;
import radiant.sispa.backend.restdto.request.CreateAccountRequestDTO;
import radiant.sispa.backend.restdto.response.AccountResponseDTO;
import radiant.sispa.backend.restdto.response.CreateAccountResponseDTO;

import java.util.List;

public interface AccountService {
    List<Account> getAllAccounts();
    AccountResponseDTO accountToAccountResponseDTO(Account account);
    Account getAccountByNo(String no) throws EntityNotFoundException;
    CreateAccountResponseDTO addAccount(CreateAccountRequestDTO accountRequestDTO, String authHeader) throws EntityExistsException, EntityNotFoundException;
    List<AccountResponseDTO> accountToAccountResponseDTO(List<Account> accounts);
    AccountResponseDTO getAccountById(Long id) throws EntityNotFoundException;
    double getTotalBalance(Account account);

    double getAccountBalance(Account account);
}
