package radiant.sispa.backend.restdto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CashFlowChartRequestDTO {
    private String accountNo;
    private String type;
}
