package radiant.sispa.backend.restcontroller;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.request.CashFlowChartRequestDTO;
import radiant.sispa.backend.restdto.request.CreateGenericDataRequestDTO;
import radiant.sispa.backend.restdto.request.CreateIncomeRequestDTO;
import radiant.sispa.backend.restdto.response.*;
import radiant.sispa.backend.restservice.TransactionCategoryService;
import radiant.sispa.backend.restservice.TransactionService;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.request.IdTransactionRequestDTO;
import radiant.sispa.backend.restdto.response.BankBalanceDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.TransactionResponseDTO;
import radiant.sispa.backend.restservice.TransactionService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @PostMapping("/detail")
    public ResponseEntity<?> getTransactionById(@Valid @RequestBody IdTransactionRequestDTO idTransactionDTO) {
        var baseResponseDTO = new BaseResponseDTO<TransactionResponseDTO>();
        TransactionResponseDTO transaction = transactionService.getTransactionById(idTransactionDTO.getId());
        if (transaction == null) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Transaction not found.");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setMessage("Success");
        baseResponseDTO.setTimestamp(new Date());
        baseResponseDTO.setData(transaction);

        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/balance-per-bank")
    public ResponseEntity<?> getBalancePerBank() {
        var baseResponseDTO = new BaseResponseDTO<List<BankBalanceDTO>>();
        try {
            List<BankBalanceDTO> balances = transactionService.getBalancePerBank();
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(balances);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Balance per bank retrieved!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve balance per bank!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/delete")
    public ResponseEntity<?> deleteTransaction(@RequestHeader(value = "Authorization", required = false) String authHeader, @RequestParam String id) {
        var baseResponseDTO = new BaseResponseDTO<List<TransactionResponseDTO>>();

        try {
            transactionService.deleteTransaction(id, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage(String.format("Berhasil menghapus transaksi dengan ID %s", id));
            baseResponseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (ConstraintViolationException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(String.format(e.getMessage()));
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (EntityNotFoundException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(String.format(e.getMessage()));
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/cash-flow/chart-data")
    public ResponseEntity<?> getCashFlowChartData(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                  @RequestBody CashFlowChartRequestDTO requestDTO,
                                                  BindingResult bindingResult) {
        var baseResponseDTO = new BaseResponseDTO<List<CashFlowChartResponseDTO>>();
        try {
            List<CashFlowChartResponseDTO> responseDTOS = transactionService.getCashFlowChartData(requestDTO);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(responseDTOS);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Cash Flow Data retrieved!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve Cash Flow Data!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
