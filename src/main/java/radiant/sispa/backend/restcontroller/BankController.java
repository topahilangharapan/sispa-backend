package radiant.sispa.backend.restcontroller;

import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.request.CreateBankRequestDTO;
import radiant.sispa.backend.restdto.request.CreateGenericDataRequestDTO;
import radiant.sispa.backend.restdto.response.*;
import radiant.sispa.backend.restservice.BankService;
import radiant.sispa.backend.restservice.WorkExperienceCategoryService;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/bank")
public class BankController {

    @Autowired
    BankService bankService;

    @PostMapping("/add")
    public ResponseEntity<?> addBank(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateBankRequestDTO bankRequestDTO) {
        var baseResponseDTO = new BaseResponseDTO<CreateBankResponseDTO>();
        try {
            CreateBankResponseDTO responseDTO = bankService.addBank(bankRequestDTO, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(responseDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Bank %s created!",
                    responseDTO.getName()));
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
            baseResponseDTO.setMessage("Failed to create Bank!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllBanks() {
        var baseResponseDTO = new BaseResponseDTO<List<BankResponseDTO>>();
        try {
            List<BankResponseDTO> bankResponseDTOS = bankService.bankToBankResponseDTO(bankService.getAllBanks());
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(bankResponseDTOS);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Banks retrieved!"));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve Banks!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
