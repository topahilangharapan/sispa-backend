package radiant.sispa.backend.restservice;

import jakarta.persistence.Column;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Freelancer;
import radiant.sispa.backend.model.Role;
import radiant.sispa.backend.model.UserModel;
import radiant.sispa.backend.model.WorkExperience;
import radiant.sispa.backend.repository.FreelancerDb;
import radiant.sispa.backend.repository.UserDb;
import radiant.sispa.backend.restdto.request.*;
import radiant.sispa.backend.restdto.response.*;
import radiant.sispa.backend.security.jwt.JwtUtils;

import javax.management.relation.RoleNotFoundException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FreelancerServiceImpl implements FreelancerService {
    @Autowired
    private FreelancerDb freelancerDb;

    @Autowired
    private UserRestService userService;

    @Autowired
    private RoleRestService roleService;

    @Autowired
    private WorkExperienceService workExperienceService;

    @Autowired
    private EducationLevelService educationLevelService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public CreateFreelancerResponseDTO addFreelancer(CreateFreelancerRequestDTO requestDTO, String authHeader) throws RoleNotFoundException, EntityNotFoundException, EntityExistsException {

        if (!userService.getUser(new UserRequestDTO(null, requestDTO.getEmail(), requestDTO.getUsername(), null, null)).isEmpty()) {
            throw new EntityExistsException("User with the same username already exists!");
        }

        String token = authHeader.substring(7);
        String createdBy = jwtUtils.getUserNameFromJwtToken(token);

        Freelancer freelancer = freelancerDb.save(createFreelancerRequestDTOToFreelancer(requestDTO, createdBy));
        List<CreateWorkExperienceResponseDTO> createWorkExperienceResponseDTOS = addWorkExperiencesToFreelancer(freelancer, requestDTO, authHeader);

        return freelancerToCreateFreelancerResponseDTO(freelancer, createWorkExperienceResponseDTOS);
    }

    private CreateFreelancerResponseDTO freelancerToCreateFreelancerResponseDTO(Freelancer freelancer, List<CreateWorkExperienceResponseDTO> createWorkExperienceResponseDTOS) {
        CreateFreelancerResponseDTO responseDTO = new CreateFreelancerResponseDTO();
        responseDTO.setEmail(freelancer.getEmail());
        responseDTO.setId(freelancer.getId());
        responseDTO.setName(freelancer.getName());
        responseDTO.setRole(freelancer.getRole().getRole());
        responseDTO.setUsername(freelancer.getUsername());
        responseDTO.setWorkExperiences(createWorkExperienceResponseDTOS);
        responseDTO.setEducation(freelancer.getEducation().getEducation());
        responseDTO.setReason(freelancer.getReason());
        responseDTO.setIsWorking(freelancer.getIsWorking());
        return responseDTO;
    }

    Freelancer createFreelancerRequestDTOToFreelancer(CreateFreelancerRequestDTO requestDTO, String createdBy) throws RoleNotFoundException, EntityNotFoundException {
        Freelancer freelancer = new Freelancer();

        freelancer.setEmail(requestDTO.getEmail());
        freelancer.setName(requestDTO.getName());
        freelancer.setUsername(requestDTO.getUsername());
        freelancer.setRole(roleService.getRoleByRoleName(requestDTO.getRole()));
        freelancer.setPassword(userService.hashPassword(requestDTO.getPassword()));
        freelancer.setCreatedBy(createdBy);
        freelancer.setAddress(requestDTO.getAddress());
        freelancer.setPhoneNumber(requestDTO.getPhoneNumber());
        freelancer.setPlaceOfBirth(requestDTO.getPlaceOfBirth());
        freelancer.setDateOfBirth(LocalDate.parse(requestDTO.getDateOfBirth()));
        freelancer.setEducation(educationLevelService.getEducationLevelByName(requestDTO.getEducation()));
        freelancer.setReason(requestDTO.getReason());
        freelancer.setWorkExperiences(new ArrayList<>());
        freelancer.setNik(userService.hashPassword(requestDTO.getNik()));
        freelancer.setIsWorking(false);

        return freelancer;
    }

    List<CreateWorkExperienceResponseDTO> addWorkExperiencesToFreelancer(Freelancer freelancer, CreateFreelancerRequestDTO requestDTO, String authHeader) throws RoleNotFoundException, EntityNotFoundException {
        List<CreateWorkExperienceRequestDTO> createWorkExperienceRequestDTOS = requestDTO.getWorkExperiences();
        List<CreateWorkExperienceResponseDTO> createWorkExperienceResponseDTOS = new ArrayList<>();

        for (CreateWorkExperienceRequestDTO createWorkExperienceRequestDTO : createWorkExperienceRequestDTOS) {
            createWorkExperienceRequestDTO.setFreelancerId(freelancer.getId());
            CreateWorkExperienceResponseDTO createWorkExperienceResponseDTO = workExperienceService.createWorkExperience(createWorkExperienceRequestDTO, authHeader);
            createWorkExperienceResponseDTOS.add(createWorkExperienceResponseDTO);
        }

        return createWorkExperienceResponseDTOS;
    }

    @Override
    public Freelancer getFreelancerById(Long id) {
        Freelancer freelancer = freelancerDb.findById(id).orElse(null);
        if (freelancer == null) {
            throw new EntityNotFoundException("Freelancer with id " + id + " not found!");
        }
        return freelancer;
    }
}
