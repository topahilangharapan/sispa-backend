package radiant.sispa.backend.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderItemResponseDTO {
    private Long id;
    private String title;
    private Long volume;
    private String unit;
    private Long pricePerUnit;
    private Long sum;
    private String description;
}
