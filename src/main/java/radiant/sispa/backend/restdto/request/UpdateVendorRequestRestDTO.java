package radiant.sispa.backend.restdto.request;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateVendorRequestRestDTO extends AddVendorRequestRestDTO{
    @NotNull
    private String id;
}
