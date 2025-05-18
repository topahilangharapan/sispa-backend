package radiant.sispa.backend.restservice;

import radiant.sispa.backend.restdto.response.TransactionResponseDTO;

public interface TransactionService {
    TransactionResponseDTO getTransactionById(String id);
}
