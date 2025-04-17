package radiant.sispa.backend.restdto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateItemRequestDTO {
    private String title;
    private Long volume;
    private String unit;
    private Long pricePerUnit;
    private String description;
}
