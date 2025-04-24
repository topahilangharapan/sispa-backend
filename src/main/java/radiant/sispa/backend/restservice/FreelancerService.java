package radiant.sispa.backend.restservice;

import radiant.sispa.backend.model.Freelancer;
import radiant.sispa.backend.restdto.request.*;
import radiant.sispa.backend.restdto.response.*;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

public interface FreelancerService {
    CreateFreelancerResponseDTO addFreelancer(CreateFreelancerRequestDTO createUserRequestDTO, String authHeader) throws RoleNotFoundException;
    Freelancer getFreelancerById(Long id);
    FreelancerResponseDTO freelancerToFreelancerResponseDTO(Freelancer freelancer);

    List<FreelancerResponseDTO> getAllFreelancer();
}
