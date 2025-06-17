package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import radiant.sispa.backend.model.TransactionCategory;
import radiant.sispa.backend.restdto.request.CreateGenericDataRequestDTO;
import radiant.sispa.backend.restdto.response.CreateGenericDataResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;

import java.util.List;

public interface TransactionCategoryService {
    List<TransactionCategory> getAllTransactionCategories();
    TransactionCategory getTransactionCategoryByName(String name) throws EntityNotFoundException;
    CreateGenericDataResponseDTO addTransactionCategory(CreateGenericDataRequestDTO name, String authHeader) throws EntityExistsException;
    List<GenericDataDTO> transactionCategoryToGenericData(List<TransactionCategory> transactionCategories);
}
