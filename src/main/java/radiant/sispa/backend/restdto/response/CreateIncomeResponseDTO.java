package radiant.sispa.backend.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import radiant.sispa.backend.restdto.request.CreateTransactionRequestDTO;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateIncomeResponseDTO extends CreateTransactionResponseDTO {
    private boolean isInterest;
}
