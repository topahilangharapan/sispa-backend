package radiant.sispa.backend.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CashFlowChartResponseDTO {
    private double amount;
    private String bank;
    private int quartal;
    private int month;
    private int year;

}
