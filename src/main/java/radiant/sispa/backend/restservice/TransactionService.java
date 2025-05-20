package radiant.sispa.backend.restservice;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import radiant.sispa.backend.restdto.response.BankBalanceDTO;
import radiant.sispa.backend.restdto.response.CashFlowChartResponseDTO;
import radiant.sispa.backend.restdto.response.TransactionResponseDTO;

public interface TransactionService {
    TransactionResponseDTO getTransactionById(String id);
    List<BankBalanceDTO> getBalancePerBank();
    void deleteTransaction(String id, String authHeader) throws EntityNotFoundException;
    ArrayList<CashFlowChartResponseDTO> getCashFlowChartData();
}
