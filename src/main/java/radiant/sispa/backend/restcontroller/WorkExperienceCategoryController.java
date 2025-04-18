package radiant.sispa.backend.restcontroller;

import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.request.CreateWorkExperienceCategoryRequestDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.CreateWorkExperienceCategoryResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;
import radiant.sispa.backend.restservice.WorkExperienceCategoryService;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/work-experience/category")
public class WorkExperienceCategoryController {

    @Autowired
    WorkExperienceCategoryService workExperienceCategoryService;

    @PostMapping("/add")
    public ResponseEntity<?> addWorkExperienceCategory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateWorkExperienceCategoryRequestDTO workExperienceCategoryRequestDTO) {
        var baseResponseDTO = new BaseResponseDTO<CreateWorkExperienceCategoryResponseDTO>();
        try {
            CreateWorkExperienceCategoryResponseDTO responseDTO = workExperienceCategoryService.addWorkExperienceCategory(workExperienceCategoryRequestDTO, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(responseDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Work Experience Category %s with id %d created!",
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
            baseResponseDTO.setMessage("Failed to create Work Experience Category!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllWorkExperienceCategory() {
        var baseResponseDTO = new BaseResponseDTO<List<GenericDataDTO>>();
        try {
            List<GenericDataDTO> genericDataDTOList = workExperienceCategoryService.workExperienceCategoryToGenericData(workExperienceCategoryService.getAllWorkExperienceCategories());
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(genericDataDTOList);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Work Experience Category retrieved!"));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve Work Experience Category!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
