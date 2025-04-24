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
import radiant.sispa.backend.restservice.EducationLevelService;
import radiant.sispa.backend.restservice.ItemStatusService;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/freelancer/education-level")
public class EducationLevelController {

    @Autowired
    EducationLevelService educationLevelService;

    @PostMapping("/create")
    public ResponseEntity<?> createEducationLevel(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateGenericDataRequestDTO requestDTO) {
        var baseResponseDTO = new BaseResponseDTO<CreateGenericDataResponseDTO>();
        try {
            CreateGenericDataResponseDTO responseDTO = educationLevelService.addEducationLevel(requestDTO, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(responseDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Education Level %s with id %d created!",
                    responseDTO.getName(),
                    responseDTO.getId()));
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
            baseResponseDTO.setMessage("Failed to create Item Status!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllEducationLevels() {
        var baseResponseDTO = new BaseResponseDTO<List<GenericDataDTO>>();
        try {
            List<GenericDataDTO> genericDataDTOList = educationLevelService.educationLevelToGenericData(educationLevelService.getAllEducationLevels());
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(genericDataDTOList);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Item Statuses retrieved!"));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve Item Status!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
