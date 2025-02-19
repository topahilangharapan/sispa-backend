package radiant.sispa.backend.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateRoleResponseDTO {
    private Long id;
    private String name;
}
