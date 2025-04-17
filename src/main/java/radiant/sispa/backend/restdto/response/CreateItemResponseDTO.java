package radiant.sispa.backend.restdto.response;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateItemResponseDTO {
    private String title;
    private Long volume;
    private String unit;
    private Long pricePerUnit;
    private Long sum;
    private String description;
}
