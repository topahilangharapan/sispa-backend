package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.*;
import radiant.sispa.backend.repository.IncomeDb;
import radiant.sispa.backend.repository.InvoiceDb;
import radiant.sispa.backend.repository.PurchaseOrderDb;
import radiant.sispa.backend.restdto.request.CreateIncomeRequestDTO;
import radiant.sispa.backend.restdto.request.CreateInvoiceRequestDTO;
import radiant.sispa.backend.restdto.response.CreateIncomeResponseDTO;
import radiant.sispa.backend.restdto.response.CreateInvoiceResponseDTO;
import radiant.sispa.backend.restdto.response.InvoiceResponseDTO;
import radiant.sispa.backend.restdto.response.PurchaseOrderItemResponseDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class IncomeServiceImpl implements IncomeService {
    @Autowired
    private IncomeDb incomeDb;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionCategoryService transactionCategoryService;

    @Override
    public CreateIncomeResponseDTO addIncome(CreateIncomeRequestDTO requestDTO, String authHeader) {

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

        Income newIncome = new Income();

        newIncome.setId(generateIncomeId(account));
        newIncome.setAmount(requestDTO.getAmount());
        newIncome.setCreatedBy(createdBy);
        newIncome.setCategory(transactionCategory);
        newIncome.setAccount(account);
        newIncome.setInterest(requestDTO.isInterest());
        newIncome.setDescription(requestDTO.getDescription());

        Income income = incomeDb.save(newIncome);

        CreateIncomeResponseDTO createIncomeResponseDTO = new CreateIncomeResponseDTO();

        createIncomeResponseDTO.setAmount(income.getAmount());
        createIncomeResponseDTO.setInterest(income.isInterest());
        createIncomeResponseDTO.setDescription(income.getDescription());
        createIncomeResponseDTO.setAccount(income.getAccount().getNo());
        createIncomeResponseDTO.setCategory(income.getCategory().getName());

        return createIncomeResponseDTO;
    }

    public String generateIncomeId(Account account) {
        String last4Account = account.getNo().length() >= 4 ?
                account.getNo().substring(account.getNo().length() - 4) : account.getNo();

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        LocalDate todayDate = LocalDate.now();
        Instant startOfDay = todayDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = todayDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        long countToday = incomeDb.countIncomeToday(startOfDay, endOfDay);

        long newCount = countToday + 1;

        String formattedCount = String.format("%04d", newCount);

        return String.format("I/%s/%s/%s/%s", last4Account, account.getBank().getName(), today, formattedCount);
    }
}
