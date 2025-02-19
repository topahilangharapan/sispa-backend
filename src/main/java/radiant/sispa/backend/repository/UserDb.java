package radiant.sispa.backend.repository;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Role;
import radiant.sispa.backend.model.UserModel;

import java.util.List;

@Repository
public interface UserDb extends JpaRepository<UserModel, Long> {
    UserModel findByUsername(String username);
    UserModel findByEmail(String email);
    List<UserModel> findByNameIgnoreCase(String name);
    List<UserModel> findByRole(Role role);
}
