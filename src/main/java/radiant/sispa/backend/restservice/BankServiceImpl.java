package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Bank;
import radiant.sispa.backend.repository.BankDb;
import radiant.sispa.backend.restdto.request.CreateBankRequestDTO;
import radiant.sispa.backend.restdto.response.BankResponseDTO;
import radiant.sispa.backend.restdto.response.CreateBankResponseDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BankServiceImpl implements BankService {
    @Autowired
    private BankDb bankDb;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public List<Bank> getAllBanks() {
        return bankDb.findAll();
    }

    @Override
    public Bank getBankByName(String name) throws EntityNotFoundException {
        Bank bank = bankDb.findByName(name.toUpperCase()).orElse(null);

        if(bank == null) {
            throw new EntityNotFoundException(String.format("Bank %s doesnt exist!", name));
        }

        return bank;
    }

    @Override
    public CreateBankResponseDTO addBank(CreateBankRequestDTO requestDTO, String authHeader) throws EntityExistsException {
        String token = authHeader.substring(7);
        String createdBy = jwtUtils.getUserNameFromJwtToken(token);

        Bank bank = new Bank();

        if (bankDb.findByName(requestDTO.getName().toUpperCase()).orElse(null) != null) {
            throw new EntityExistsException(String.format("Bank %s already exists", requestDTO.getName()));
        }

        bank.setName(requestDTO.getName().toUpperCase());
        bank.setCreatedBy(createdBy);
        bank.setInterestRate(requestDTO.getInterestRate());
        bankDb.save(bank);

        CreateBankResponseDTO responseDTO = new CreateBankResponseDTO();
        responseDTO.setName(bank.getName());
        responseDTO.setInterestRate(bank.getInterestRate());
        responseDTO.setAdminFee(bank.getAdminFee());

        return responseDTO;
    }

    @Override
    public List<BankResponseDTO> bankToBankResponseDTO(List<Bank> banks) {
        List<BankResponseDTO> bankResponseDTOS = new ArrayList<>();
        for (Bank bank : banks) {
            BankResponseDTO bankResponseDTO = new BankResponseDTO();

            bankResponseDTO.setId(bank.getId());
            bankResponseDTO.setName(bank.getName());
            bankResponseDTO.setInterestRate(bank.getInterestRate());
            bankResponseDTO.setAdminFee(bank.getAdminFee());
            bankResponseDTO.setCreatedBy(bank.getCreatedBy());
            bankResponseDTO.setCreatedAt(bank.getCreatedAt());
            bankResponseDTO.setUpdatedBy(bank.getUpdatedBy());
            bankResponseDTO.setUpdatedAt(bank.getUpdatedAt());
            bankResponseDTO.setDeletedBy(bank.getDeletedBy());
            bankResponseDTO.setDeletedAt(bank.getDeletedAt());

            bankResponseDTOS.add(bankResponseDTO);
        }
        return bankResponseDTOS;
    }
}
