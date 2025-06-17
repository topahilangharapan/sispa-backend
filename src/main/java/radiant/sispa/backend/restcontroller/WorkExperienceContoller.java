package radiant.sispa.backend.restcontroller;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.request.CreateWorkExperienceRequestDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.CreateWorkExperienceResponseDTO;
import radiant.sispa.backend.restservice.WorkExperienceService;

import java.util.Date;


@RestController
@RequestMapping("/api/work-experience")
public class WorkExperienceContoller {
    @Autowired
    WorkExperienceService workExperienceService;

    @PostMapping("/add")
    public ResponseEntity<?> addWorkExperience(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateWorkExperienceRequestDTO requestDTO) {
        var baseResponseDTO = new BaseResponseDTO<CreateWorkExperienceResponseDTO>();

        try {
            CreateWorkExperienceResponseDTO responseDTO = workExperienceService.createWorkExperience(requestDTO);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(responseDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Work Experience created!"));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            if (e instanceof EntityExistsException || e instanceof EntityNotFoundException) {
                baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
                baseResponseDTO.setTimestamp(new Date());
                baseResponseDTO.setMessage(e.getMessage());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to create Work Experience!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
