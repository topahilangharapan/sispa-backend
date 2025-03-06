package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Role;
import radiant.sispa.backend.repository.RoleDb;
import radiant.sispa.backend.restdto.request.CreateRoleRequestDTO;
import radiant.sispa.backend.restdto.response.CreateRoleResponseDTO;
import radiant.sispa.backend.restdto.response.CreateUserResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;

import javax.management.relation.RoleNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleRestServiceImpl implements RoleRestService {
    @Autowired
    private RoleDb roleDb;

    @Override
    public List<Role> getAllRoles() {
        return roleDb.findAll();
    }

    @Override
    public Role getRoleByRoleName(String name) throws RoleNotFoundException {
        Role role = roleDb.findByRole(name.toUpperCase()).orElse(null);

        if(role == null) {
            throw new RoleNotFoundException(String.format("Role %s doesnt exist!", name));
        }

        return role;
    }

    @Override
    public CreateRoleResponseDTO addRole(CreateRoleRequestDTO requestDTO) throws EntityExistsException {
        Role role = new Role();

        if (roleDb.findByRole(requestDTO.getName().toUpperCase()).orElse(null) != null) {
            throw new EntityExistsException(String.format("Role %s already exists", requestDTO.getName()));
        }

        role.setRole(requestDTO.getName().toUpperCase());
        roleDb.save(role);

        CreateRoleResponseDTO responseDTO = new CreateRoleResponseDTO();
        responseDTO.setId(role.getId());
        responseDTO.setName(role.getRole());

        return responseDTO;
    }

    @Override
    public List<GenericDataDTO> roleToGenericData(List<Role> listRole) {
        List<GenericDataDTO> genericDataDTOs = new ArrayList<>();
        for (Role role : listRole) {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            genericDataDTO.setId(role.getId());
            genericDataDTO.setName(role.getRole());
            genericDataDTOs.add(genericDataDTO);
        }
        return genericDataDTOs;
    }
}
