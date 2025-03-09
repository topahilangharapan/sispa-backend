package radiant.sispa.backend.restdto.request;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateClientRequestRestDTO extends AddClientRequestRestDTO{
    @NotNull
    private String id;
}
