package radiant.sispa.backend.restcontroller;

import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.request.CreateItemStatusRequestDTO;
import radiant.sispa.backend.restdto.request.CreateWorkExperienceCategoryRequestDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.CreateItemStatusResponseDTO;
import radiant.sispa.backend.restdto.response.CreateWorkExperienceCategoryResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;
import radiant.sispa.backend.restservice.ItemStatusService;
import radiant.sispa.backend.restservice.WorkExperienceCategoryService;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/item/status")
public class ItemStatusController {

    @Autowired
    ItemStatusService itemStatusService;

    @PostMapping("/add")
    public ResponseEntity<?> addItemStatus(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateItemStatusRequestDTO requestDTO) {
        var baseResponseDTO = new BaseResponseDTO<CreateItemStatusResponseDTO>();
        try {
            CreateItemStatusResponseDTO responseDTO = itemStatusService.addItemStatus(requestDTO, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(responseDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Item Status %s with id %d created!",
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
    public ResponseEntity<?> getAllItemStatuses() {
        var baseResponseDTO = new BaseResponseDTO<List<GenericDataDTO>>();
        try {
            List<GenericDataDTO> genericDataDTOList = itemStatusService.itemStatusToGenericData(itemStatusService.getAllItemStatuses());
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
