package radiant.sispa.backend.restdto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateTransactionRequestDTO {
    private double amount;
    private String description;
    private String account;
    private String category;
    private String transactionDate;
}
