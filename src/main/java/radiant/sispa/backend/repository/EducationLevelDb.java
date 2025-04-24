package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.EducationLevel;
import radiant.sispa.backend.model.ItemStatus;

import java.util.Optional;

@Repository
public interface EducationLevelDb extends JpaRepository<EducationLevel, Long> {
    Optional<EducationLevel> findByEducation(String education);
}
