package radiant.sispa.backend.restservice;

import radiant.sispa.backend.restdto.request.CreateWorkExperienceRequestDTO;
import radiant.sispa.backend.restdto.response.CreateWorkExperienceResponseDTO;

public interface WorkExperienceService {
    CreateWorkExperienceResponseDTO createWorkExperience(CreateWorkExperienceRequestDTO createWorkExperienceRequestDTO);
}
