package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Role;
import radiant.sispa.backend.model.UserModel;
import radiant.sispa.backend.repository.UserDb;
import radiant.sispa.backend.restdto.request.CreateUserRequestDTO;
import radiant.sispa.backend.restdto.request.PasswordChangeRequestDTO;
import radiant.sispa.backend.restdto.request.UserProfileRequestDTO;
import radiant.sispa.backend.restdto.request.UserRequestDTO;
import radiant.sispa.backend.restdto.response.CreateUserResponseDTO;
import radiant.sispa.backend.restdto.response.PasswordChangeResponseDTO;
import radiant.sispa.backend.restdto.response.UserProfileResponseDTO;
import radiant.sispa.backend.restdto.response.UserResponseDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;

import javax.management.relation.RoleNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserRestServiceImpl implements UserRestService {
    @Autowired
    private UserDb userDb;

    @Autowired
    private RoleRestService roleService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public CreateUserResponseDTO addUser(CreateUserRequestDTO requestDTO, String authHeader) throws RoleNotFoundException, EntityExistsException {

        if (!getUser(new UserRequestDTO(null, requestDTO.getEmail(), requestDTO.getUsername(), null, null)).isEmpty()) {
            throw new EntityExistsException("User with the same username or email already exists!");
        }

        String token = authHeader.substring(7);
        String createdBy = jwtUtils.getUserNameFromJwtToken(token);

        UserModel user = new UserModel();
        user.setEmail(requestDTO.getEmail());
        user.setName(requestDTO.getName());
        user.setUsername(requestDTO.getUsername());
        user.setRole(roleService.getRoleByRoleName(requestDTO.getRole()));
        user.setPassword(hashPassword(requestDTO.getPassword()));
        user.setCreatedBy(createdBy);
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
                userResponseDTOs.add(userToUserResponseDto(user));
                return userResponseDTOs;
            }
        }

        if (email != null) {
            UserModel user = userDb.findByEmail(email);

            if (user != null) {
                userResponseDTOs.add(userToUserResponseDto(user));
                return userResponseDTOs;
            }
        }

        if (username != null) {
            UserModel user = userDb.findByUsername(username);

            if (user != null) {
                userResponseDTOs.add(userToUserResponseDto(user));
                return userResponseDTOs;
            }
        }

        if (name != null) {
            List<UserModel> users = userDb.findByNameIgnoreCase(name);

            if (users != null) {
                for (UserModel user : users) {
                    userResponseDTOs.add(userToUserResponseDto(user));
                }

                return userResponseDTOs;
            }
        }

        if (requestDTO.getRole() != null) {
            Role role = roleService.getRoleByRoleName(requestDTO.getRole());
            List<UserModel> users = userDb.findByRole(role);

            for (UserModel user : users) {
                userResponseDTOs.add(userToUserResponseDto(user));
            }

            return userResponseDTOs;
        }

        return userResponseDTOs;
    }

    public UserResponseDTO userToUserResponseDto(UserModel user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setId(user.getId());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setUsername(user.getUsername());
        userResponseDTO.setName(user.getName());
        userResponseDTO.setRole(user.getRole().getRole());
        userResponseDTO.setCreatedBy(user.getCreatedBy());
        userResponseDTO.setAddress(user.getAddress());
        userResponseDTO.setPhoneNumber(user.getPhoneNumber());
        userResponseDTO.setPlaceOfBirth(user.getPlaceOfBirth());
        userResponseDTO.setDateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null);
        Optional.ofNullable(user.getCreatedAt())
                .map(Timestamp::from)
                .ifPresent(userResponseDTO::setCreatedAt);
        userResponseDTO.setUpdatedBy(user.getUpdatedBy());
        Optional.ofNullable(user.getUpdatedAt())
                .map(Timestamp::from)
                .ifPresent(userResponseDTO::setUpdatedAt);
        userResponseDTO.setDeletedBy(user.getDeletedBy());
        Optional.ofNullable(user.getDeletedAt())
                .map(Timestamp::from)
                .ifPresent(userResponseDTO::setDeletedAt);

        return userResponseDTO;
    }

    @Override
    public UserProfileResponseDTO updateUserProfile(UserRequestDTO userRequestDTO, UserProfileRequestDTO profileRequestDTO) {
        Optional<UserModel> optionalUser = userDb.findById(userRequestDTO.getId());
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        UserModel user = optionalUser.get();

        // Existing updates
        user.setEmail(profileRequestDTO.getEmail());
        user.setName(profileRequestDTO.getName());
        user.setAddress(profileRequestDTO.getAddress());
        user.setPhoneNumber(profileRequestDTO.getPhoneNumber());

        // NEW: placeOfBirth
        if (profileRequestDTO.getPlaceOfBirth() != null) {
            user.setPlaceOfBirth(profileRequestDTO.getPlaceOfBirth());
        }

        // NEW: dateOfBirth
        // The dateOfBirth is a String in the request. We can parse it:
        if (profileRequestDTO.getDateOfBirth() != null) {
            // e.g. "2025-03-14"
            user.setDateOfBirth(LocalDate.parse(profileRequestDTO.getDateOfBirth()));
            // you could handle exceptions if the format is invalid
        }

        userDb.save(user);

        // Return the updated user
        UserProfileResponseDTO response = new UserProfileResponseDTO();
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setAddress(user.getAddress());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setPlaceOfBirth(user.getPlaceOfBirth());
        response.setDateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null);

        return response;
    }

    @Override
    public PasswordChangeResponseDTO changePassword(PasswordChangeRequestDTO requestDTO) {
        // Get user by ID
        Optional<UserModel> optionalUser = userDb.findById(requestDTO.getUserId());
        
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        UserModel user = optionalUser.get();
        
        // Hash the new password
        String hashedPassword = hashPassword(requestDTO.getNewPassword());
        
        // Update user's password
        user.setPassword(hashedPassword);
        user.setUpdatedAt(java.time.Instant.now());
        
        // Save the updated user
        userDb.save(user);
        
        // Return success response
        PasswordChangeResponseDTO response = new PasswordChangeResponseDTO();
        response.setSuccess(true);
        response.setMessage("Password changed successfully");
        
        return response;
    }
}
