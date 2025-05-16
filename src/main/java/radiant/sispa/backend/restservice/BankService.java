package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import radiant.sispa.backend.model.Bank;
import radiant.sispa.backend.model.WorkExperienceCategory;
import radiant.sispa.backend.restdto.request.CreateBankRequestDTO;
import radiant.sispa.backend.restdto.request.CreateGenericDataRequestDTO;
import radiant.sispa.backend.restdto.response.BankResponseDTO;
import radiant.sispa.backend.restdto.response.CreateBankResponseDTO;
import radiant.sispa.backend.restdto.response.CreateGenericDataResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;

import java.util.List;

public interface BankService {
    List<Bank> getAllBanks();
    Bank getBankByName(String name) throws EntityNotFoundException;
    CreateBankResponseDTO addBank(CreateBankRequestDTO name, String authHeader) throws EntityExistsException;
    List<BankResponseDTO> bankToBankResponseDTO(List<Bank> banks);
}
