package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import radiant.sispa.backend.model.WorkExperienceCategory;
import radiant.sispa.backend.restdto.request.CreateGenericDataRequestDTO;
import radiant.sispa.backend.restdto.response.CreateGenericDataResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;

import java.util.List;

public interface WorkExperienceCategoryService {
    List<WorkExperienceCategory> getAllWorkExperienceCategories();
    WorkExperienceCategory getWorkExperienceCategoryByName(String name) throws EntityNotFoundException;
    CreateGenericDataResponseDTO addWorkExperienceCategory(CreateGenericDataRequestDTO name, String authHeader) throws EntityExistsException;
    List<GenericDataDTO> workExperienceCategoryToGenericData(List<WorkExperienceCategory> workExperienceCategories);
}
