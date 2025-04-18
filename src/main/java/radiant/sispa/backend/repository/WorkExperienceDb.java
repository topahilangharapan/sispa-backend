package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.WorkExperience;
import radiant.sispa.backend.model.WorkExperienceCategory;

@Repository
public interface WorkExperienceDb extends JpaRepository<WorkExperience, Long> {

}
