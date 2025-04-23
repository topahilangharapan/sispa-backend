package radiant.sispa.backend.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.request.CreateItemRequestDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.CreateItemResponseDTO;
import radiant.sispa.backend.restdto.response.FinalReportResponseDTO;
import radiant.sispa.backend.restdto.response.ItemResponseDTO;
import radiant.sispa.backend.restservice.ItemService;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Autowired
    ItemService itemService;

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
}
