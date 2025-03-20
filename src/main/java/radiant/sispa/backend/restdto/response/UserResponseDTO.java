package radiant.sispa.backend.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String username;
    private String name;
    private String role;
    private String address;
    private String phoneNumber;
    private String placeOfBirth;
    private String dateOfBirth;
    private Timestamp createdAt;
    private String createdBy;
    private Timestamp updatedAt;
    private String updatedBy;
    private Timestamp deletedAt;
    private String deletedBy;
}
