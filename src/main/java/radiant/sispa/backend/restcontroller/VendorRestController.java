package radiant.sispa.backend.restcontroller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.request.AddVendorRequestRestDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.VendorResponseDTO;
import radiant.sispa.backend.restservice.VendorRestService;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vendor")
public class VendorRestController {
    @Autowired
    VendorRestService vendorRestService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/add")
    public ResponseEntity<?> addVendor(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @Valid @RequestBody AddVendorRequestRestDTO vendorDTO, BindingResult bindingResult) {
        var baseResponseDTO = new BaseResponseDTO<VendorResponseDTO>();

        String token = "";
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        if (bindingResult.hasFieldErrors()) {
            String errorMessages = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages += error.getDefaultMessage() + "; ";
            }

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        VendorResponseDTO vendor = vendorRestService.addVendor(vendorDTO, jwtUtils.getUserNameFromJwtToken(token));

        baseResponseDTO.setStatus(HttpStatus.CREATED.value());
        baseResponseDTO.setData(vendor);
        baseResponseDTO.setMessage(String.format("Berhasil menambahkan vendor dengan ID %s", vendor.getId()));
        baseResponseDTO.setTimestamp(new Date());

        return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/viewall")
    public ResponseEntity<BaseResponseDTO<List<VendorResponseDTO>>> listVendor() {
        List<VendorResponseDTO> listVendor = vendorRestService.getAllVendor();

        var baseResponseDTO = new BaseResponseDTO<List<VendorResponseDTO>>();
        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(listVendor);
        baseResponseDTO.setMessage("Berhasil mengambil daftar vendor");
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}/delete")
    public ResponseEntity<?> deleteVendor(@PathVariable String id) {
        var baseResponseDTO = new BaseResponseDTO<List<VendorResponseDTO>>();

        try {
            vendorRestService.deleteVendor(id);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage(String.format("Berhasil menghapus vendor dengan ID %s", id));
            baseResponseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (ConstraintViolationException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(String.format(e.getMessage()));
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (EntityNotFoundException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(String.format(e.getMessage()));
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }
    }
}