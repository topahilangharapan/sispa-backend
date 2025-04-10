package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import radiant.sispa.backend.model.Role;
import radiant.sispa.backend.restdto.request.CreateRoleRequestDTO;
import radiant.sispa.backend.restdto.response.CreateRoleResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

public interface RoleRestService {
    List<Role> getAllRoles();
    Role getRoleByRoleName(String name) throws RoleNotFoundException;
    CreateRoleResponseDTO addRole(CreateRoleRequestDTO name, String authHeader) throws EntityExistsException;
    List<GenericDataDTO> roleToGenericData(List<Role> role);
}
