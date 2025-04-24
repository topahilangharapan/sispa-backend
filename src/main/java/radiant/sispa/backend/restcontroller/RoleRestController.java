package radiant.sispa.backend.restcontroller;

import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.request.CreateGenericDataRequestDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.CreateGenericDataResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;
import radiant.sispa.backend.restservice.RoleRestService;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/role")
public class RoleRestController {
    @Autowired
    RoleRestService roleService;

    @PostMapping("/add")
    public ResponseEntity<?> addRole(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateGenericDataRequestDTO roleRequestDTO) {
        var baseResponseDTO = new BaseResponseDTO<CreateGenericDataResponseDTO>();
        try {
            CreateGenericDataResponseDTO roleResponseDTO = roleService.addRole(roleRequestDTO, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(roleResponseDTO);
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

    @GetMapping("/all")
    public ResponseEntity<?> getAllRoles() {
        var baseResponseDTO = new BaseResponseDTO<List<GenericDataDTO>>();
        try {
            List<GenericDataDTO> genericDataDTOList = roleService.roleToGenericData(roleService.getAllRoles());
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(genericDataDTOList);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Role retrieved!"));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve role!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
