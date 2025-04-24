package radiant.sispa.backend.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import radiant.sispa.backend.restdto.request.CreateWorkExperienceRequestDTO;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateFreelancerResponseDTO extends CreateUserResponseDTO{
    private String education;
    private List<CreateWorkExperienceResponseDTO> workExperiences;
    private String reason;
    private Boolean isWorking;
}
