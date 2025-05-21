package radiant.sispa.backend.restcontroller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.request.CreateExpenseRequestDTO;
import radiant.sispa.backend.restdto.request.CreateIncomeRequestDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.CreateExpenseResponseDTO;
import radiant.sispa.backend.restdto.response.CreateIncomeResponseDTO;
import radiant.sispa.backend.restservice.ExpenseService;
import radiant.sispa.backend.restservice.IncomeService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/transaction/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;


    @GetMapping("/all")
    public ResponseEntity<?> getAllExpense() {
        var baseResponseDTO = new BaseResponseDTO<List<CreateExpenseResponseDTO>>();
        try {
            List<CreateExpenseResponseDTO> allExpenses = expenseService.getAllExpense();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(allExpenses);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("List of expenses retrieved!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve expense list!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addExpense(
            @Valid @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateExpenseRequestDTO requestDTO,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<CreateExpenseResponseDTO>();

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
            CreateExpenseResponseDTO responseDTO = expenseService.addExpense(requestDTO, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(responseDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Expense berhasil dicatat!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            if (e instanceof EntityNotFoundException) {
                baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponseDTO.setTimestamp(new Date());
                baseResponseDTO.setMessage(e.getMessage());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
            } else if (e instanceof IllegalStateException) {
                baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
                baseResponseDTO.setTimestamp(new Date());
                baseResponseDTO.setMessage(e.getMessage());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Gagal mencatat Expense!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<?> getExpensesByAccount(
            @PathVariable Long accountId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        var baseResponseDTO = new BaseResponseDTO<List<CreateExpenseResponseDTO>>();

        try {
            List<CreateExpenseResponseDTO> expenses = expenseService.getExpensesByAccount(accountId);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(expenses);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Expenses retrieved!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve expenses!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
