package radiant.sispa.backend.restcontroller;

import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.request.CreateGenericDataRequestDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.CreateGenericDataResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;
import radiant.sispa.backend.restservice.TransactionCategoryService;
import radiant.sispa.backend.restservice.WorkExperienceCategoryService;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/transaction/category")
public class TransactionCategoryController {

    @Autowired
    TransactionCategoryService transactionCategoryService;

    @PostMapping("/add")
    public ResponseEntity<?> addTransactionCategory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateGenericDataRequestDTO requestDTO) {
        var baseResponseDTO = new BaseResponseDTO<CreateGenericDataResponseDTO>();
        try {
            CreateGenericDataResponseDTO responseDTO = transactionCategoryService.addTransactionCategory(requestDTO, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(responseDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Transaction Category %s with id %d created!",
                    responseDTO.getName(),
                    responseDTO.getId()));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            if (e instanceof EntityExistsException) {
                baseResponseDTO.setStatus(HttpStatus.CONFLICT.value());
                baseResponseDTO.setTimestamp(new Date());
                baseResponseDTO.setMessage(e.getMessage());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.CONFLICT);
            }
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to create Transaction Category!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTransactionCategories() {
        var baseResponseDTO = new BaseResponseDTO<List<GenericDataDTO>>();
        try {
            List<GenericDataDTO> genericDataDTOList = transactionCategoryService.transactionCategoryToGenericData(transactionCategoryService.getAllTransactionCategories());
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(genericDataDTOList);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Transaction Category retrieved!"));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve Transaction Category!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
