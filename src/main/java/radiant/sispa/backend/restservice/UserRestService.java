package radiant.sispa.backend.restservice;

import radiant.sispa.backend.restdto.request.CreateUserRequestDTO;
import radiant.sispa.backend.restdto.request.PasswordChangeRequestDTO;
import radiant.sispa.backend.restdto.request.UserProfileRequestDTO;
import radiant.sispa.backend.restdto.request.UserRequestDTO;
import radiant.sispa.backend.restdto.response.CreateUserResponseDTO;
import radiant.sispa.backend.restdto.response.PasswordChangeResponseDTO;
import radiant.sispa.backend.restdto.response.UserProfileResponseDTO;
import radiant.sispa.backend.restdto.response.UserResponseDTO;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

public interface UserRestService {
    CreateUserResponseDTO addUser(CreateUserRequestDTO createUserRequestDTO, String authHeader) throws RoleNotFoundException;
    String hashPassword(String password);
    List<UserResponseDTO> getUser(UserRequestDTO userRequestDTO) throws RoleNotFoundException;
    UserProfileResponseDTO updateUserProfile(UserRequestDTO userRequestDTO, UserProfileRequestDTO profileRequestDTO);
    PasswordChangeResponseDTO changePassword(PasswordChangeRequestDTO requestDTO);
}
