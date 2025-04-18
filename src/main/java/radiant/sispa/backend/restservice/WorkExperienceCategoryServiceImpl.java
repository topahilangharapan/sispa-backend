package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Role;
import radiant.sispa.backend.model.WorkExperienceCategory;
import radiant.sispa.backend.repository.RoleDb;
import radiant.sispa.backend.repository.WorkExperienceCategoryDb;
import radiant.sispa.backend.restdto.request.CreateRoleRequestDTO;
import radiant.sispa.backend.restdto.request.CreateWorkExperienceCategoryRequestDTO;
import radiant.sispa.backend.restdto.response.CreateRoleResponseDTO;
import radiant.sispa.backend.restdto.response.CreateWorkExperienceCategoryResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;

import javax.management.relation.RoleNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class WorkExperienceCategoryServiceImpl implements WorkExperienceCategoryService {
    @Autowired
    private WorkExperienceCategoryDb workExperienceCategoryDb;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public List<WorkExperienceCategory> getAllWorkExperienceCategories() {
        return workExperienceCategoryDb.findAll();
    }

    @Override
    public WorkExperienceCategory getWorkExperienceCategoryByName(String name) throws EntityNotFoundException {
        WorkExperienceCategory workExperienceCategory = workExperienceCategoryDb.findByCategory(name.toUpperCase()).orElse(null);

        if(workExperienceCategory == null) {
            throw new EntityNotFoundException(String.format("Work Category %s doesnt exist!", name));
        }

        return workExperienceCategory;
    }

    @Override
    public CreateWorkExperienceCategoryResponseDTO addWorkExperienceCategory(CreateWorkExperienceCategoryRequestDTO requestDTO, String authHeader) throws EntityExistsException {
        String token = authHeader.substring(7);
        String createdBy = jwtUtils.getUserNameFromJwtToken(token);

        WorkExperienceCategory workExperienceCategory = new WorkExperienceCategory();

        if (workExperienceCategoryDb.findByCategory(requestDTO.getName().toUpperCase()).orElse(null) != null) {
            throw new EntityExistsException(String.format("Work Category %s already exists", requestDTO.getName()));
        }

        workExperienceCategory.setCategory(requestDTO.getName().toUpperCase());
        workExperienceCategory.setCreatedBy(createdBy);
        workExperienceCategoryDb.save(workExperienceCategory);

        CreateWorkExperienceCategoryResponseDTO responseDTO = new CreateWorkExperienceCategoryResponseDTO();
        responseDTO.setId(workExperienceCategory.getId());
        responseDTO.setName(workExperienceCategory.getCategory());

        return responseDTO;
    }

    @Override
    public List<GenericDataDTO> workExperienceCategoryToGenericData(List<WorkExperienceCategory> workExperienceCategories) {
        List<GenericDataDTO> genericDataDTOs = new ArrayList<>();
        for (WorkExperienceCategory workExperienceCategory : workExperienceCategories) {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            genericDataDTO.setId(workExperienceCategory.getId());
            genericDataDTO.setName(workExperienceCategory.getCategory());
            genericDataDTOs.add(genericDataDTO);
        }
        return genericDataDTOs;
    }
}
