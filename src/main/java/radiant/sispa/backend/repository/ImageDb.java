package radiant.sispa.backend.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import radiant.sispa.backend.model.Client;
import radiant.sispa.backend.model.Image;

import java.util.List;

@Transactional
public interface ImageDb extends JpaRepository<Image, String> {
    Image findById(long id);

}
