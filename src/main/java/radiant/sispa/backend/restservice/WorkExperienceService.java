package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import radiant.sispa.backend.model.WorkExperienceCategory;
import radiant.sispa.backend.restdto.request.CreateWorkExperienceCategoryRequestDTO;
import radiant.sispa.backend.restdto.request.CreateWorkExperienceRequestDTO;
import radiant.sispa.backend.restdto.response.CreateWorkExperienceCategoryResponseDTO;
import radiant.sispa.backend.restdto.response.CreateWorkExperienceResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;

import java.util.List;

public interface WorkExperienceService {
    CreateWorkExperienceResponseDTO createWorkExperience(CreateWorkExperienceRequestDTO createWorkExperienceRequestDTO, String authHeader);
}
