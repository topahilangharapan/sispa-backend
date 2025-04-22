package radiant.sispa.backend.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import radiant.sispa.backend.restdto.request.CreateCategoryRequestDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.CategoryResponseDTO;
import radiant.sispa.backend.restservice.CategoryService;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/category")
public class CategoryRestController {

    @Autowired
    CategoryService categoryService;

    @PostMapping("/add")
    public ResponseEntity<?> addCategory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateCategoryRequestDTO createCategoryRequestDTO) {

        var baseResponseDTO = new BaseResponseDTO<CategoryResponseDTO>();
        try {
            CategoryResponseDTO createCategoryResponseDTO = categoryService.createCategory(createCategoryRequestDTO, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(createCategoryResponseDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Category generated!"));
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
        var baseResponseDTO = new BaseResponseDTO<List<CategoryResponseDTO>>();
        try {
            List<CategoryResponseDTO> allOrders = categoryService.getAllCategory();

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
