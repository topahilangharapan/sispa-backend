package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import radiant.sispa.backend.model.Freelancer;
import radiant.sispa.backend.repository.FreelancerDb;
import radiant.sispa.backend.restdto.request.*;
import radiant.sispa.backend.restdto.response.*;
import radiant.sispa.backend.security.jwt.JwtUtils;

import javax.management.relation.RoleNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
    public CreateFreelancerResponseDTO addFreelancer(CreateFreelancerRequestDTO requestDTO) throws RoleNotFoundException, EntityNotFoundException, EntityExistsException {

        if (!userService.getUser(new UserRequestDTO(null, null, requestDTO.getUsername(), null, null)).isEmpty()) {
            throw new EntityExistsException("User with the same username already exists!");
        }

        if (!userService.getUser(new UserRequestDTO(null, requestDTO.getEmail(), null, null, null)).isEmpty()) {
            throw new EntityExistsException("User with the same email already exists!");
        }

        Freelancer freelancer = freelancerDb.save(createFreelancerRequestDTOToFreelancer(requestDTO));
        List<CreateWorkExperienceResponseDTO> createWorkExperienceResponseDTOS = addWorkExperiencesToFreelancer(freelancer, requestDTO);

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

    Freelancer createFreelancerRequestDTOToFreelancer(CreateFreelancerRequestDTO requestDTO) throws RoleNotFoundException, EntityNotFoundException {
        Freelancer freelancer = new Freelancer();

        freelancer.setEmail(requestDTO.getEmail());
        freelancer.setName(requestDTO.getName());
        freelancer.setUsername(requestDTO.getUsername());
        freelancer.setRole(roleService.getRoleByRoleName(requestDTO.getRole()));
        freelancer.setPassword(userService.hashPassword(requestDTO.getPassword()));
        freelancer.setCreatedBy(requestDTO.getUsername());
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

    List<CreateWorkExperienceResponseDTO> addWorkExperiencesToFreelancer(Freelancer freelancer, CreateFreelancerRequestDTO requestDTO) throws RoleNotFoundException, EntityNotFoundException {
        List<CreateWorkExperienceRequestDTO> createWorkExperienceRequestDTOS = requestDTO.getWorkExperiences();
        List<CreateWorkExperienceResponseDTO> createWorkExperienceResponseDTOS = new ArrayList<>();

        for (CreateWorkExperienceRequestDTO createWorkExperienceRequestDTO : createWorkExperienceRequestDTOS) {
            createWorkExperienceRequestDTO.setFreelancerId(freelancer.getId());
            CreateWorkExperienceResponseDTO createWorkExperienceResponseDTO = workExperienceService.createWorkExperience(createWorkExperienceRequestDTO);
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

    @Override
    public List<FreelancerResponseDTO> getAllFreelancerApplicants() {
        List<Freelancer> freelancerApplicants = freelancerDb.findByDeletedAtNullAndApprovedAtNull();
        List<FreelancerResponseDTO> listFreelancerResponseDTO = new ArrayList<>();
        for (Freelancer freelancer : freelancerApplicants) {
            var freelancerResponseDTO = freelancerToFreelancerResponseDTO(freelancer);
            listFreelancerResponseDTO.add(freelancerResponseDTO);
        }
        return listFreelancerResponseDTO;
    }

    public FreelancerResponseDTO freelancerToFreelancerResponseDTO(Freelancer freelancer) {
        FreelancerResponseDTO freelancerResponseDTO = new FreelancerResponseDTO();

        freelancerResponseDTO.setId(freelancer.getId());
        freelancerResponseDTO.setEmail(freelancer.getEmail());
        freelancerResponseDTO.setUsername(freelancer.getUsername());
        freelancerResponseDTO.setName(freelancer.getName());
        freelancerResponseDTO.setRole(freelancer.getRole().getRole());
        freelancerResponseDTO.setCreatedBy(freelancer.getCreatedBy());
        freelancerResponseDTO.setAddress(freelancer.getAddress());
        freelancerResponseDTO.setPhoneNumber(freelancer.getPhoneNumber());
        freelancerResponseDTO.setPlaceOfBirth(freelancer.getPlaceOfBirth());
        freelancerResponseDTO.setDateOfBirth(freelancer.getDateOfBirth() != null ? freelancer.getDateOfBirth().toString() : null);
        Optional.ofNullable(freelancer.getCreatedAt())
                .map(Timestamp::from)
                .ifPresent(freelancerResponseDTO::setCreatedAt);
        freelancerResponseDTO.setUpdatedBy(freelancer.getUpdatedBy());
        Optional.ofNullable(freelancer.getUpdatedAt())
                .map(Timestamp::from)
                .ifPresent(freelancerResponseDTO::setUpdatedAt);
        freelancerResponseDTO.setDeletedBy(freelancer.getDeletedBy());
        Optional.ofNullable(freelancer.getDeletedAt())
                .map(Timestamp::from)
                .ifPresent(freelancerResponseDTO::setDeletedAt);

        freelancerResponseDTO.setEducation(freelancer.getEducation().getEducation());
        freelancerResponseDTO.setWorkExperiences(freelancer.getWorkExperiences());
        freelancerResponseDTO.setReason(freelancer.getReason());
        freelancerResponseDTO.setNik(freelancer.getNik());
        freelancerResponseDTO.setIsWorking(freelancer.getIsWorking());
        freelancerResponseDTO.setApprovedAt(freelancer.getApprovedAt());

        return freelancerResponseDTO;
    }

    @Override
    public List<FreelancerResponseDTO> getAllFreelancer() {
        List<Freelancer> listFreelancer = freelancerDb.findByDeletedAtNullAndApprovedAtNotNull();

        List<FreelancerResponseDTO> listFreelancerResponseDTO = new ArrayList<>();
        for (Freelancer freelancer : listFreelancer) {
            var freelancerResponseDTO = freelancerToFreelancerResponseDTO(freelancer);
            listFreelancerResponseDTO.add(freelancerResponseDTO);
        }
        return listFreelancerResponseDTO;
    }

}
