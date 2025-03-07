package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Role;
import radiant.sispa.backend.model.UserModel;
import radiant.sispa.backend.repository.UserDb;
import radiant.sispa.backend.restdto.request.CreateUserRequestDTO;
import radiant.sispa.backend.restdto.request.UserRequestDTO;
import radiant.sispa.backend.restdto.response.CreateUserResponseDTO;
import radiant.sispa.backend.restdto.response.UserResponseDTO;

import javax.management.relation.RoleNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserRestServiceImpl implements UserRestService {
    @Autowired
    private UserDb userDb;

    @Autowired
    private RoleRestService roleService;

    @Override
    public CreateUserResponseDTO addUser(CreateUserRequestDTO requestDTO) throws RoleNotFoundException, EntityExistsException {

        if (!getUser(new UserRequestDTO(null, requestDTO.getEmail(), requestDTO.getUsername(), null, null)).isEmpty()) {
            throw new EntityExistsException("User with the same username already exists!");
        }

        UserModel user = new UserModel();
        user.setEmail(requestDTO.getEmail());
        user.setName(requestDTO.getName());
        user.setUsername(requestDTO.getUsername());
        user.setRole(roleService.getRoleByRoleName(requestDTO.getRole()));
        user.setPassword(hashPassword(requestDTO.getPassword()));
        userDb.save(user);

        CreateUserResponseDTO responseDTO = new CreateUserResponseDTO();
        responseDTO.setEmail(user.getEmail());
        responseDTO.setId(user.getId());
        responseDTO.setName(user.getName());
        responseDTO.setRole(user.getRole().getRole());
        responseDTO.setUsername(user.getUsername());
        return responseDTO;
    }

    @Override
    public String hashPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    @Override
    public List<UserResponseDTO> getUser(UserRequestDTO requestDTO) throws RoleNotFoundException {
        Long id = requestDTO.getId();
        String email = requestDTO.getEmail();
        String username = requestDTO.getUsername();
        String name = requestDTO.getName();

        List<UserResponseDTO> userResponseDTOs = new ArrayList<>();

        if (id != null) {
            UserModel user = userDb.findById(id).orElse(null);

            if (user != null) {
                userResponseDTOs.add(new UserResponseDTO(user.getId(), user.getEmail(), user.getUsername(), user.getName(), user.getRole().getRole()));
                return userResponseDTOs;
            }
        }

        if (email != null) {
            UserModel user = userDb.findByEmail(email);

            if (user != null) {
                userResponseDTOs.add(new UserResponseDTO(user.getId(), user.getEmail(), user.getUsername(), user.getName(), user.getRole().getRole()));
                return userResponseDTOs;
            }
        }

        if (username != null) {
            UserModel user = userDb.findByUsername(username);

            if (user != null) {
                userResponseDTOs.add(new UserResponseDTO(user.getId(), user.getEmail(), user.getUsername(), user.getName(), user.getRole().getRole()));
                return userResponseDTOs;
            }
        }

        if (name != null) {
            List<UserModel> users = userDb.findByNameIgnoreCase(name);

            if (users != null) {
                for (UserModel user : users) {
                    userResponseDTOs.add(new UserResponseDTO(user.getId(), user.getEmail(), user.getUsername(), user.getName(), user.getRole().getRole()));
                }

                return userResponseDTOs;
            }
        }

        if (requestDTO.getRole() != null) {
            Role role = roleService.getRoleByRoleName(requestDTO.getRole());
            List<UserModel> users = userDb.findByRole(role);

            for (UserModel user : users) {
                userResponseDTOs.add(new UserResponseDTO(user.getId(), user.getEmail(), user.getUsername(), user.getName(), user.getRole().getRole()));
            }

            return userResponseDTOs;
        }

        return userResponseDTOs;
    }
}
