package radiant.sispa.backend.restdto.request;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateItemStatusRequestRestDTO {
    private Long itemId;
    private Long idItemStatus;
}
