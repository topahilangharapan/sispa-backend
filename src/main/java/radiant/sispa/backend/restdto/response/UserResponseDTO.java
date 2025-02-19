package radiant.sispa.backend.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String username;
    private String name;
    private String role;
}
