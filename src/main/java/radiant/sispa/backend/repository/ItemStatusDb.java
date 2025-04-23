package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.ItemStatus;
import radiant.sispa.backend.model.WorkExperienceCategory;

import java.util.Optional;

@Repository
public interface ItemStatusDb extends JpaRepository<ItemStatus, Long> {
    Optional<ItemStatus> findByStatus(String status);
}
