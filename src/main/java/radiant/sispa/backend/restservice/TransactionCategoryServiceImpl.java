package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.TransactionCategory;
import radiant.sispa.backend.model.WorkExperienceCategory;
import radiant.sispa.backend.repository.TransactionCategoryDb;
import radiant.sispa.backend.repository.WorkExperienceCategoryDb;
import radiant.sispa.backend.restdto.request.CreateGenericDataRequestDTO;
import radiant.sispa.backend.restdto.response.CreateGenericDataResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TransactionCategoryServiceImpl implements TransactionCategoryService {
    @Autowired
    private TransactionCategoryDb transactionCategoryDb;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public List<TransactionCategory> getAllTransactionCategories() {
        return transactionCategoryDb.findAll();
    }

    @Override
    public TransactionCategory getTransactionCategoryByName(String name) throws EntityNotFoundException {
        TransactionCategory transactionCategory = transactionCategoryDb.findByName(name.toUpperCase()).orElse(null);

        if(transactionCategory == null) {
            throw new EntityNotFoundException(String.format("Transaction Category %s doesnt exist!", name));
        }

        return transactionCategory;
    }

    @Override
    public CreateGenericDataResponseDTO addTransactionCategory(CreateGenericDataRequestDTO requestDTO, String authHeader) throws EntityExistsException {
        String token = authHeader.substring(7);
        String createdBy = jwtUtils.getUserNameFromJwtToken(token);

        TransactionCategory transactionCategory = new TransactionCategory();

        if (transactionCategoryDb.findByName(requestDTO.getName().toUpperCase()).orElse(null) != null) {
            throw new EntityExistsException(String.format("Transaction Category %s already exists", requestDTO.getName()));
        }

        transactionCategory.setName(requestDTO.getName().toUpperCase());
        transactionCategory.setCreatedBy(createdBy);
        transactionCategoryDb.save(transactionCategory);

        CreateGenericDataResponseDTO responseDTO = new CreateGenericDataResponseDTO();
        responseDTO.setId(transactionCategory.getId());
        responseDTO.setName(transactionCategory.getName());

        return responseDTO;
    }

    @Override
    public List<GenericDataDTO> transactionCategoryToGenericData(List<TransactionCategory> transactionCategories) {
        List<GenericDataDTO> genericDataDTOs = new ArrayList<>();
        for (TransactionCategory transactionCategory : transactionCategories) {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            genericDataDTO.setId(transactionCategory.getId());
            genericDataDTO.setName(transactionCategory.getName());
            genericDataDTOs.add(genericDataDTO);
        }
        return genericDataDTOs;
    }
}
