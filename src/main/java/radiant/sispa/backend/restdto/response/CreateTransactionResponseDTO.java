package radiant.sispa.backend.restdto.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateTransactionResponseDTO {
    private String id;
    private double amount;
    private String description;
    private String transactionDate;
    private String account;
    private String category;
    private String createdAt;
}
