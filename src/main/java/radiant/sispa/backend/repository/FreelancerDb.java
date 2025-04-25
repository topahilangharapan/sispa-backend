package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Freelancer;
import radiant.sispa.backend.model.Role;
import radiant.sispa.backend.model.UserModel;

import java.util.List;

@Repository
public interface FreelancerDb extends JpaRepository<Freelancer, Long> {
    List<Freelancer> findByDeletedAtNullAndApprovedAtNull();
    List<Freelancer> findByDeletedAtNullAndApprovedAtNotNull();
    List<Freelancer> findByDeletedAtNullAndApprovedAtNotNullOrderByApprovedAtDesc();
    List<Freelancer> findByDeletedAtNullAndApprovedAtNullOrderByCreatedAtDesc();
}
