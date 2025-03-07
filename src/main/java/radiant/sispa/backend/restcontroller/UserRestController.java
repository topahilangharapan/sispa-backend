package radiant.sispa.backend.restcontroller;

import jakarta.persistence.EntityExistsException;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.request.CreateUserRequestDTO;
import radiant.sispa.backend.restdto.request.UserRequestDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.CreateUserResponseDTO;
import radiant.sispa.backend.restdto.response.UserResponseDTO;
import radiant.sispa.backend.restservice.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.management.relation.RoleNotFoundException;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/user")
public class UserRestController {
    @Autowired
    UserRestService userService;

    @PostMapping("/add")
    public ResponseEntity<?> addUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateUserRequestDTO userRequestDTO) {
        var baseResponseDTO = new BaseResponseDTO<CreateUserResponseDTO>();

        try {
            CreateUserResponseDTO userResponseDTO = userService.addUser(userRequestDTO, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(userResponseDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("User %s with id %d created!",
                    userResponseDTO.getUsername(),
                    userResponseDTO.getId()));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            if (e instanceof RoleNotFoundException || e instanceof EntityExistsException) {
                baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
                baseResponseDTO.setTimestamp(new Date());
                baseResponseDTO.setMessage(e.getMessage());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to create User!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/get")
    public ResponseEntity<?> getUser(@RequestBody UserRequestDTO userRequestDTO) {
        var baseResponseDTO = new BaseResponseDTO<List<UserResponseDTO>>();
        try {
            List<UserResponseDTO> userResponseDTO = userService.getUser(userRequestDTO);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(userResponseDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("User retrieved!"));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            if (e instanceof RoleNotFoundException) {
                baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
                baseResponseDTO.setTimestamp(new Date());
                baseResponseDTO.setMessage(e.getMessage());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve User!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
