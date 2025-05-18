package radiant.sispa.backend.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.TransactionResponseDTO;
import radiant.sispa.backend.restservice.TransactionService;

import java.util.Date;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @GetMapping("/detail")
    public ResponseEntity<?> getTransactionById(@RequestParam String id) {
        var baseResponseDTO = new BaseResponseDTO<TransactionResponseDTO>();
        TransactionResponseDTO transaction = transactionService.getTransactionById(id);

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
}
