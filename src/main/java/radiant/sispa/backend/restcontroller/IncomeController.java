package radiant.sispa.backend.restcontroller;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.model.Income;
import radiant.sispa.backend.restdto.request.CreateIncomeRequestDTO;
import radiant.sispa.backend.restdto.request.CreateInvoiceRequestDTO;
import radiant.sispa.backend.restdto.response.*;
import radiant.sispa.backend.restservice.IncomeService;
import radiant.sispa.backend.restservice.InvoiceService;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/transaction/income")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllIncome() {
        var baseResponseDTO = new BaseResponseDTO<List<CreateIncomeResponseDTO>>();
        try {
            List<CreateIncomeResponseDTO> allIncomes = incomeService.getAllIncome();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(allIncomes);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("List of incomes retrieved!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve income list!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addIncome(
            @Valid
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateIncomeRequestDTO requestDTO,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<CreateIncomeResponseDTO>();

        if (bindingResult.hasFieldErrors()) {
            String errorMessages = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages += error.getDefaultMessage() + "; ";
            }

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            CreateIncomeResponseDTO responseDTO = incomeService.addIncome(requestDTO, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(responseDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Income berhasil dicatat!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            if (e instanceof EntityNotFoundException) {
                baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponseDTO.setTimestamp(new Date());
                baseResponseDTO.setMessage(e.getMessage());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
            }
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Gagal mencatat Income!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<?> getIncomesByAccount(
            @PathVariable Long accountId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        var baseResponseDTO = new BaseResponseDTO<List<CreateIncomeResponseDTO>>();

        try {
            List<CreateIncomeResponseDTO> incomes = incomeService.getIncomeByAccount(accountId);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(incomes);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Incomes retrieved!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve incomes!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
