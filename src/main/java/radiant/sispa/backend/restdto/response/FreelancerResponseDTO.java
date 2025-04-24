package radiant.sispa.backend.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import radiant.sispa.backend.model.WorkExperience;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FreelancerResponseDTO extends UserResponseDTO {
    private String education;
    private List<WorkExperience> workExperiences;
    private String reason;
    private String nik;
    private Boolean isWorking;
    private Instant approvedAt;
}
