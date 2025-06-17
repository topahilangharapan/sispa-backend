package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Role;
import radiant.sispa.backend.model.WorkExperienceCategory;

import java.util.Optional;

@Repository
public interface WorkExperienceCategoryDb extends JpaRepository<WorkExperienceCategory, Long> {
    Optional<WorkExperienceCategory> findByCategory(String category);
}
