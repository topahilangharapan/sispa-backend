package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.ItemCategory;

import java.util.List;

@Repository
public interface CategoryDb extends JpaRepository<ItemCategory, Long> {
    List<ItemCategory> findAllByDeletedAtIsNull();
    ItemCategory findByNameIgnoreCaseAndDeletedAtNull(String name);
}
