package radiant.sispa.backend.restdto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateBankRequestDTO {
    private String name;
    private double interestRate;
    private double adminFee;
}
