package radiant.sispa.backend.restdto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateIncomeRequestDTO extends CreateTransactionRequestDTO{
    private boolean isInterest;
}
