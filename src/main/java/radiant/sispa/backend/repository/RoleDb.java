package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Role;

import java.util.Optional;

@Repository
public interface RoleDb extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(String role);
}
