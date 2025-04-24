package radiant.sispa.backend.restcontroller;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.request.CreateItemRequestDTO;
import radiant.sispa.backend.restdto.request.CreateRoleRequestDTO;
import radiant.sispa.backend.restdto.request.UpdateItemRequestRestDTO;
import radiant.sispa.backend.restdto.response.*;
import radiant.sispa.backend.restservice.ItemService;
import radiant.sispa.backend.restservice.RoleRestService;
import radiant.sispa.backend.security.jwt.JwtUtils;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.CreateItemResponseDTO;
import radiant.sispa.backend.restdto.response.ItemResponseDTO;
import radiant.sispa.backend.restservice.ItemService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Autowired
    ItemService itemService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/create")
    public ResponseEntity<?> createItem(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateItemRequestDTO createItemRequestDTO) {

        var baseResponseDTO = new BaseResponseDTO<CreateItemResponseDTO>();
        try {
            CreateItemResponseDTO createItemResponseDTO = itemService.createItem(createItemRequestDTO, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(createItemResponseDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Item generated!"));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Failed to generate item: %s !", e.getMessage()));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateItem(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @Valid @RequestBody UpdateItemRequestRestDTO itemDTO, BindingResult bindingResult) {
        var baseResponseDTO = new BaseResponseDTO<ItemResponseDTO>();
                String token = "";
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

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

        ItemResponseDTO updatedItem = itemService.updateItem(itemDTO.getId(), itemDTO, jwtUtils.getUserNameFromJwtToken(token));
        if (updatedItem == null){
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Item not found or cannot be updated");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setMessage(String.format("Item with ID %s has been updated", updatedItem.getId()));
        baseResponseDTO.setTimestamp(new Date());
        baseResponseDTO.setData(updatedItem);
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable("id") Long id){
        var baseResponseDTO = new BaseResponseDTO<ItemResponseDTO>();
        ItemResponseDTO item = itemService.detailItem(id);

        if (item == null){
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("item not found");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setMessage("Success");
        baseResponseDTO.setTimestamp(new Date());
        baseResponseDTO.setData(item);

        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllItems() {
        var baseResponseDTO = new BaseResponseDTO<List<ItemResponseDTO>>();

        try {
            List<ItemResponseDTO> allReports = itemService.getAllItems();
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(allReports);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("List of Items retrieved!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve Items!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/delete")
    public ResponseEntity<?> deleteItem(@PathVariable("id") Long id) {
        var baseResponseDTO = new BaseResponseDTO<List<ItemResponseDTO>>();

        try {
            itemService.deleteItem(id);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage(String.format("Berhasil menghapus item dengan ID %s", id));
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
