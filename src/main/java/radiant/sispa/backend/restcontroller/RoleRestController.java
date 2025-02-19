package radiant.sispa.backend.restcontroller;

import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import radiant.sispa.backend.restdto.request.CreateRoleRequestDTO;
import radiant.sispa.backend.restdto.request.CreateUserRequestDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.CreateRoleResponseDTO;
import radiant.sispa.backend.restdto.response.CreateUserResponseDTO;
import radiant.sispa.backend.restservice.RoleRestService;
import radiant.sispa.backend.restservice.UserRestService;

import java.util.Date;


@RestController
@RequestMapping("/api/role")
public class RoleRestController {
    @Autowired
    RoleRestService roleService;

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody CreateRoleRequestDTO roleRequestDTO) {
        var baseResponseDTO = new BaseResponseDTO<CreateRoleRequestDTO>();
        try {
            CreateRoleResponseDTO roleResponseDTO = roleService.addRole(roleRequestDTO);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(roleRequestDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Role %s with id %d created!",
                    roleResponseDTO.getName(),
                    roleResponseDTO.getId()));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            if (e instanceof EntityExistsException) {
                baseResponseDTO.setStatus(HttpStatus.CONFLICT.value());
                baseResponseDTO.setTimestamp(new Date());
                baseResponseDTO.setMessage(e.getMessage());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.CONFLICT);
            }
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to create Role!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
