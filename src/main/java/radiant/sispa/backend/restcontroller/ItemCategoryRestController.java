package radiant.sispa.backend.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.request.CreateItemCategoryRequestDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.ItemCategoryResponseDTO;
import radiant.sispa.backend.restservice.ItemCategoryService;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/category")
public class ItemCategoryRestController {

    @Autowired
    ItemCategoryService categoryService;

    @PostMapping("/add")
    public ResponseEntity<?> addCategory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateItemCategoryRequestDTO createCategoryRequestDTO) {

        var baseResponseDTO = new BaseResponseDTO<ItemCategoryResponseDTO>();
        try {
            ItemCategoryResponseDTO createCategoryResponseDTO = categoryService.createCategory(createCategoryRequestDTO, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(createCategoryResponseDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Item Category generated!"));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Failed to generate category category: %s !", e.getMessage()));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllCategory() {
        var baseResponseDTO = new BaseResponseDTO<List<ItemCategoryResponseDTO>>();
        try {
            List<ItemCategoryResponseDTO> allOrders = categoryService.getAllCategory();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(allOrders);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("List of categories retrieved!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve categorys!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
