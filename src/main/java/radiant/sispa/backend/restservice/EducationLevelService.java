package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import radiant.sispa.backend.model.EducationLevel;
import radiant.sispa.backend.model.WorkExperienceCategory;
import radiant.sispa.backend.restdto.request.CreateGenericDataRequestDTO;
import radiant.sispa.backend.restdto.request.CreateWorkExperienceCategoryRequestDTO;
import radiant.sispa.backend.restdto.response.CreateGenericDataResponseDTO;
import radiant.sispa.backend.restdto.response.CreateWorkExperienceCategoryResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;

import java.util.List;

public interface EducationLevelService {
    List<EducationLevel> getAllEducationLevels();
    EducationLevel getEducationLevelByName(String name) throws EntityNotFoundException;
    CreateGenericDataResponseDTO addEducationLevel(CreateGenericDataRequestDTO name, String authHeader) throws EntityExistsException;
    List<GenericDataDTO> educationLevelToGenericData(List<EducationLevel> educationLevels);
}
