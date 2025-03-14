package radiant.sispa.backend.restdto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserProfileRequestDTO {
    private String email;
    private String name;
    private String address;
    private String phoneNumber;
    private String placeOfBirth;
    private String dateOfBirth;
}
