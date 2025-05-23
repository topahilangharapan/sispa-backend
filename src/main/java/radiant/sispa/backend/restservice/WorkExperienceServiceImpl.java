package radiant.sispa.backend.restservice;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Freelancer;
import radiant.sispa.backend.model.WorkExperience;
import radiant.sispa.backend.repository.WorkExperienceDb;
import radiant.sispa.backend.restdto.request.CreateWorkExperienceRequestDTO;
import radiant.sispa.backend.restdto.response.CreateWorkExperienceResponseDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.time.LocalDate;

@Service
@Transactional
public class WorkExperienceServiceImpl implements WorkExperienceService {
    @Autowired
    private WorkExperienceDb workExperienceDb;

    @Autowired
    private WorkExperienceCategoryService workExperienceCategoryService;

    @Autowired
    private FreelancerService freelancerService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public CreateWorkExperienceResponseDTO createWorkExperience(CreateWorkExperienceRequestDTO createWorkExperienceRequestDTO) {
        WorkExperience workExperience = workExperienceDb.save(createWorkExperienceDTOToWorkExperience(createWorkExperienceRequestDTO));

        CreateWorkExperienceResponseDTO createWorkExperienceResponseDTO = new CreateWorkExperienceResponseDTO();

        createWorkExperienceResponseDTO.setCategory(workExperience.getCategory().getCategory());
        createWorkExperienceResponseDTO.setTitle(workExperience.getTitle());
        createWorkExperienceResponseDTO.setDescription(workExperience.getDescription());
        createWorkExperienceResponseDTO.setStartDate(workExperience.getStartDate().toString());
        createWorkExperienceResponseDTO.setIsStillWorking(workExperience.isStillWorking());

        if (workExperience.getEndDate() != null) {
            createWorkExperienceResponseDTO.setEndDate(workExperience.getEndDate().toString());
        } else {
            createWorkExperienceResponseDTO.setEndDate(null);
        }


        return createWorkExperienceResponseDTO;
    }

    private WorkExperience createWorkExperienceDTOToWorkExperience(CreateWorkExperienceRequestDTO createWorkExperienceRequestDTO) {
        WorkExperience workExperience = new WorkExperience();
        Freelancer freelancer = freelancerService.getFreelancerById(createWorkExperienceRequestDTO.getFreelancerId());

        workExperience.setFreelancer(freelancer);
        workExperience.setCategory(workExperienceCategoryService.getWorkExperienceCategoryByName(createWorkExperienceRequestDTO.getCategory()));
        workExperience.setTitle(createWorkExperienceRequestDTO.getTitle());
        workExperience.setDescription(createWorkExperienceRequestDTO.getDescription());
        workExperience.setStillWorking(createWorkExperienceRequestDTO.getIsStillWorking());
        workExperience.setStartDate(LocalDate.parse(createWorkExperienceRequestDTO.getStartDate()));

        if (createWorkExperienceRequestDTO.getEndDate() != null && !createWorkExperienceRequestDTO.getEndDate().isEmpty()) {
            workExperience.setEndDate(LocalDate.parse(createWorkExperienceRequestDTO.getEndDate()));
        } else {
            workExperience.setEndDate(null);
        }

        workExperience.setCreatedBy(freelancer.getUsername());

        return workExperience;
    }

}
