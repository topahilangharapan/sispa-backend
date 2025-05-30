package radiant.sispa.backend.restservice;

import radiant.sispa.backend.model.Freelancer;
import radiant.sispa.backend.restdto.request.CreateFreelancerRequestDTO;
import radiant.sispa.backend.restdto.response.*;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

public interface FreelancerService {
    CreateFreelancerResponseDTO addFreelancer(CreateFreelancerRequestDTO createUserRequestDTO) throws RoleNotFoundException;
    Freelancer getFreelancerById(Long id);
    List<FreelancerResponseDTO> getAllFreelancerApplicants();
    FreelancerResponseDTO freelancerToFreelancerResponseDTO(Freelancer freelancer);

    List<FreelancerResponseDTO> getAllFreelancer();

    FreelancerResponseDTO approveFreelancer(Long id, String updatedBy);
    
    FreelancerResponseDTO updateWorkingStatus(Long id, boolean isWorking, String updatedBy);

    String extractUsername(String token);

    FreelancerResponseDTO rejectFreelancer(Long id, String updatedBy);
}
