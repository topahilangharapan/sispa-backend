package radiant.sispa.backend.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeResponseDTO {
    private boolean success;
    private String message;
} 