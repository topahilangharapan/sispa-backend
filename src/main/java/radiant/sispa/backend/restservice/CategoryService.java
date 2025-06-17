package radiant.sispa.backend.restservice;


import radiant.sispa.backend.restdto.request.CreateCategoryRequestDTO;
import radiant.sispa.backend.restdto.response.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {
    CategoryResponseDTO createCategory(CreateCategoryRequestDTO createCategoryRequestDTO, String authHeader);
    List<CategoryResponseDTO> getAllCategory();
    CategoryResponseDTO getCategoryByName(String categoryName);
}
