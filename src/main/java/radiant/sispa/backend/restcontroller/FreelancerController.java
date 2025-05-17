package radiant.sispa.backend.restcontroller;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.request.*;
import radiant.sispa.backend.restdto.request.CreateFreelancerRequestDTO;
import radiant.sispa.backend.restdto.response.*;
import radiant.sispa.backend.restservice.FreelancerService;

import javax.management.relation.RoleNotFoundException;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/freelancer")
public class FreelancerController {
    @Autowired
    FreelancerService freelancerService;

    @PostMapping("/add")
    public ResponseEntity<?> addFreelancer(
            @RequestBody CreateFreelancerRequestDTO freelancerRequestDTO) {
        var baseResponseDTO = new BaseResponseDTO<CreateFreelancerResponseDTO>();

        try {
            CreateFreelancerResponseDTO responseDTO = freelancerService.addFreelancer(freelancerRequestDTO);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(responseDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Freelancer %s with id %d created!",
                    responseDTO.getUsername(),
                    responseDTO.getId()));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();

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
    @GetMapping("/viewall-applicants")
    public ResponseEntity<?> getAllFreelancerApplicants() {
        var baseResponseDTO = new BaseResponseDTO<List<FreelancerResponseDTO>>();
        try {
            List<FreelancerResponseDTO> allFreelancerApplicants = freelancerService.getAllFreelancerApplicants();
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(allFreelancerApplicants);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("List of Freelancer Applicants retrieved!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve Freelancer Applicants!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/viewall")
    public ResponseEntity<BaseResponseDTO<List<FreelancerResponseDTO>>> listFreelancer() {
        List<FreelancerResponseDTO> listFreelancer = freelancerService.getAllFreelancer();

        var baseResponseDTO = new BaseResponseDTO<List<FreelancerResponseDTO>>();
        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(listFreelancer);
        baseResponseDTO.setMessage("Berhasil mengambil daftar freelancer");
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<FreelancerResponseDTO>> getFreelancerById(
            @PathVariable Long id) {
        var baseResponseDTO = new BaseResponseDTO<FreelancerResponseDTO>();
        
        try {
            var freelancer = freelancerService.getFreelancerById(id);
            var freelancerResponseDTO = freelancerService.freelancerToFreelancerResponseDTO(freelancer);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(freelancerResponseDTO);
            baseResponseDTO.setMessage("Berhasil mendapatkan data freelancer");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Gagal mendapatkan data freelancer");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/{id}/approve")
    public ResponseEntity<BaseResponseDTO<FreelancerResponseDTO>> approveFreelancer(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        var baseResponseDTO = new BaseResponseDTO<FreelancerResponseDTO>();
        
        try {
            // Get username from auth header for audit
            String username = authHeader.startsWith("Bearer ") ? 
                    freelancerService.extractUsername(authHeader.substring(7)) : "system";
            
            FreelancerResponseDTO updatedFreelancer = freelancerService.approveFreelancer(id, username);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(updatedFreelancer);
            baseResponseDTO.setMessage("Freelancer berhasil disetujui");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Gagal menyetujui freelancer: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/update-working-status")
    public ResponseEntity<BaseResponseDTO<FreelancerResponseDTO>> updateWorkingStatus(
            @PathVariable Long id,
            @RequestBody UpdateWorkingStatusRequestDTO requestDTO,
            @RequestHeader("Authorization") String authHeader) {
        var baseResponseDTO = new BaseResponseDTO<FreelancerResponseDTO>();
        
        try {
            // Add logging to debug the received request
            System.out.println("Received work status update request: " + requestDTO.isWorking());
            
            // Get username from auth header for audit
            String username = authHeader.startsWith("Bearer ") ? 
                    freelancerService.extractUsername(authHeader.substring(7)) : "system";
            
            FreelancerResponseDTO updatedFreelancer = freelancerService.updateWorkingStatus(
                id, 
                requestDTO.isWorking(),  // This should correctly call the isWorking() method
                username
            );
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(updatedFreelancer);
            baseResponseDTO.setMessage("Status freelancer berhasil diubah");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Add better error logging
            e.printStackTrace();
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Gagal mengubah status freelancer: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<BaseResponseDTO<FreelancerResponseDTO>> rejectFreelancer(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        var baseResponseDTO = new BaseResponseDTO<FreelancerResponseDTO>();
        
        try {
            // Get username from auth header for audit
            String username = authHeader.startsWith("Bearer ") ? 
                    freelancerService.extractUsername(authHeader.substring(7)) : "system";
            
            FreelancerResponseDTO updatedFreelancer = freelancerService.rejectFreelancer(id, username);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(updatedFreelancer);
            baseResponseDTO.setMessage("Freelancer application rejected");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to reject freelancer: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
