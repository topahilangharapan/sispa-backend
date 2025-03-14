package radiant.sispa.backend.restdto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequestDTO {
    private UserRequestDTO userRequestDTO;
    private UserProfileRequestDTO profileRequestDTO;
}
