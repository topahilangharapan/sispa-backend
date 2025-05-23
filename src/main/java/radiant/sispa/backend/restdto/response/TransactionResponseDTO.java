package radiant.sispa.backend.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import radiant.sispa.backend.model.TransactionCategory;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionResponseDTO {
    private String id;
    private Double amount;
    private String description;
    private String transactionDate;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private AccountResponseDTO account;
    private TransactionCategory category;
}