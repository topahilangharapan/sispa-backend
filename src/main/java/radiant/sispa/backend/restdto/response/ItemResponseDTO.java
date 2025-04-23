package radiant.sispa.backend.restdto.response;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDTO {
    private Long id;
    private String title;
    private String unit;
    private Long pricePerUnit;
    private String description;
    private String category;
    private String status;

}
