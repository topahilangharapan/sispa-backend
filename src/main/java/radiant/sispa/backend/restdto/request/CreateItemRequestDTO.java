package radiant.sispa.backend.restdto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateItemRequestDTO {
    private String title;
    private String unit;
    private Long pricePerUnit;
    private String description;
    private String category;
}
