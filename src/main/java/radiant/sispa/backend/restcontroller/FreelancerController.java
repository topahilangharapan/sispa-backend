package radiant.sispa.backend.restcontroller;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.request.*;
import radiant.sispa.backend.restdto.response.*;
import radiant.sispa.backend.restservice.FreelancerService;

import javax.management.relation.RoleNotFoundException;
import java.util.Date;


@RestController
@RequestMapping("/api/freelancer")
public class FreelancerController {
    @Autowired
    FreelancerService freelancerService;

    @PostMapping("/add")
    public ResponseEntity<?> addFreelancer(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateFreelancerRequestDTO freelancerRequestDTO) {
        var baseResponseDTO = new BaseResponseDTO<CreateFreelancerResponseDTO>();

        try {
            CreateFreelancerResponseDTO responseDTO = freelancerService.addFreelancer(freelancerRequestDTO, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(responseDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Freelancer %s with id %d created!",
                    responseDTO.getUsername(),
                    responseDTO.getId()));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            if (e instanceof RoleNotFoundException || e instanceof EntityExistsException || e instanceof EntityNotFoundException) {
                baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
                baseResponseDTO.setTimestamp(new Date());
                baseResponseDTO.setMessage(e.getMessage());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to create Freelancer!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
