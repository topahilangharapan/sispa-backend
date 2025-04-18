package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import radiant.sispa.backend.model.Role;
import radiant.sispa.backend.model.WorkExperienceCategory;
import radiant.sispa.backend.restdto.request.CreateRoleRequestDTO;
import radiant.sispa.backend.restdto.request.CreateWorkExperienceCategoryRequestDTO;
import radiant.sispa.backend.restdto.response.CreateRoleResponseDTO;
import radiant.sispa.backend.restdto.response.CreateWorkExperienceCategoryResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

public interface WorkExperienceCategoryService {
    List<WorkExperienceCategory> getAllWorkExperienceCategories();
    WorkExperienceCategory getWorkExperienceCategoryByName(String name) throws EntityNotFoundException;
    CreateWorkExperienceCategoryResponseDTO addWorkExperienceCategory(CreateWorkExperienceCategoryRequestDTO name, String authHeader) throws EntityExistsException;
    List<GenericDataDTO> workExperienceCategoryToGenericData(List<WorkExperienceCategory> workExperienceCategories);
}
