package radiant.sispa.backend.restdto.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateFreelancerRequestDTO extends CreateUserRequestDTO {
    private String address;
    private String phoneNumber;
    private String placeOfBirth;
    private String dateOfBirth;
    private String education;
    private List<CreateWorkExperienceRequestDTO> workExperiences;
    private String reason;
    private String nik;
}
