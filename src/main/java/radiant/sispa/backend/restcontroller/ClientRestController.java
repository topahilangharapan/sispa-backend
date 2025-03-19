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
import radiant.sispa.backend.restdto.request.AddClientRequestRestDTO;
import radiant.sispa.backend.restdto.request.UpdateClientRequestRestDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.ClientResponseDTO;
import radiant.sispa.backend.restservice.ClientRestService;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/client")
public class ClientRestController {
    @Autowired
    ClientRestService clientRestService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/add")
    public ResponseEntity<?> addClient(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @Valid @RequestBody AddClientRequestRestDTO clientDTO, BindingResult bindingResult) {
        var baseResponseDTO = new BaseResponseDTO<ClientResponseDTO>();

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

        ClientResponseDTO client = clientRestService.addClient(clientDTO, jwtUtils.getUserNameFromJwtToken(token));

        baseResponseDTO.setStatus(HttpStatus.CREATED.value());
        baseResponseDTO.setData(client);
        baseResponseDTO.setMessage(String.format("Berhasil menambahkan klien dengan ID %s", client.getId()));
        baseResponseDTO.setTimestamp(new Date());

        return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/delete")
    public ResponseEntity<?> deleteClient(@PathVariable("id") String id) {
        var baseResponseDTO = new BaseResponseDTO<List<ClientResponseDTO>>();

        try {
            clientRestService.deleteClient(id);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage(String.format("Berhasil menghapus klien dengan ID %s", id));
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

    @PutMapping("/update")
    public ResponseEntity<?> updateClient(@Valid @RequestBody UpdateClientRequestRestDTO clientDTO, BindingResult bindingResult) {
        var baseResponseDTO = new BaseResponseDTO<ClientResponseDTO>();

        if (bindingResult.hasFieldErrors()) {
            String errorMessages = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
        
        ClientResponseDTO updatedClient = clientRestService.updateClient(clientDTO.getId(), clientDTO);

        if (updatedClient == null){
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Client not found or cannot be updated");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setMessage(String.format("Client with ID %s has been updated", updatedClient.getId()));
        baseResponseDTO.setTimestamp(new Date());
        baseResponseDTO.setData(updatedClient);

        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getClientById(@PathVariable("id") String id) {
        var baseResponseDTO = new BaseResponseDTO<ClientResponseDTO>();
        ClientResponseDTO client = clientRestService.getClientById(id);

        if (client == null){
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Client not found");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setMessage("Success");
        baseResponseDTO.setTimestamp(new Date());
        baseResponseDTO.setData(client);

        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/viewall")
    public ResponseEntity<BaseResponseDTO<List<ClientResponseDTO>>> listClient() {
        List<ClientResponseDTO> listClient = clientRestService.getAllClient();

        var baseResponseDTO = new BaseResponseDTO<List<ClientResponseDTO>>();
        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(listClient);
        baseResponseDTO.setMessage("Berhasil mengambil daftar client");
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

}