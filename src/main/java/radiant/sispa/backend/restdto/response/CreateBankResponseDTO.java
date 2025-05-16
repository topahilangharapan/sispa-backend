package radiant.sispa.backend.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateBankResponseDTO{
    private String name;
    private double interestRate;
    private double adminFee;
}
