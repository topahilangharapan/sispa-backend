package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Category;

import java.util.List;

@Repository
public interface CategoryDb extends JpaRepository<Category, Long> {
    List<Category> findAllByDeletedAtIsNull();
    Category findByNameIgnoreCaseAndDeletedAtNull(String name);
}
