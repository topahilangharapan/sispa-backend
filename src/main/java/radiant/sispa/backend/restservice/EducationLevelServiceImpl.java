package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.EducationLevel;
import radiant.sispa.backend.model.WorkExperienceCategory;
import radiant.sispa.backend.repository.EducationLevelDb;
import radiant.sispa.backend.repository.WorkExperienceCategoryDb;
import radiant.sispa.backend.restdto.request.CreateGenericDataRequestDTO;
import radiant.sispa.backend.restdto.request.CreateWorkExperienceCategoryRequestDTO;
import radiant.sispa.backend.restdto.response.CreateGenericDataResponseDTO;
import radiant.sispa.backend.restdto.response.CreateWorkExperienceCategoryResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class EducationLevelServiceImpl implements EducationLevelService {
    @Autowired
    private EducationLevelDb educationLevelDb;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public List<EducationLevel> getAllEducationLevels() {
        return educationLevelDb.findAll();
    }

    @Override
    public EducationLevel getEducationLevelByName(String name) throws EntityNotFoundException {
        EducationLevel educationLevel = educationLevelDb.findByEducation(name.toUpperCase()).orElse(null);

        if(educationLevel == null) {
            throw new EntityNotFoundException(String.format("Education Level %s doesnt exist!", name));
        }

        return educationLevel;
    }

    @Override
    public CreateGenericDataResponseDTO addEducationLevel(CreateGenericDataRequestDTO requestDTO, String authHeader) throws EntityExistsException {
        String token = authHeader.substring(7);
        String createdBy = jwtUtils.getUserNameFromJwtToken(token);

        EducationLevel educationLevel = new EducationLevel();

        if (educationLevelDb.findByEducation(requestDTO.getName().toUpperCase()).orElse(null) != null) {
            throw new EntityExistsException(String.format("Education Level %s already exists", requestDTO.getName()));
        }

        educationLevel.setEducation(requestDTO.getName().toUpperCase());
        educationLevel.setCreatedBy(createdBy);
        educationLevelDb.save(educationLevel);

        CreateGenericDataResponseDTO responseDTO = new CreateGenericDataResponseDTO();
        responseDTO.setId(educationLevel.getId());
        responseDTO.setName(educationLevel.getEducation());

        return responseDTO;
    }

    @Override
    public List<GenericDataDTO> educationLevelToGenericData(List<EducationLevel> educationLevels) {
        List<GenericDataDTO> genericDataDTOs = new ArrayList<>();
        for (EducationLevel educationLevel : educationLevels) {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            genericDataDTO.setId(educationLevel.getId());
            genericDataDTO.setName(educationLevel.getEducation());
            genericDataDTOs.add(genericDataDTO);
        }
        return genericDataDTOs;
    }
}
