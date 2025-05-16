package radiant.sispa.backend.restservice;


import radiant.sispa.backend.restdto.request.CreateItemCategoryRequestDTO;
import radiant.sispa.backend.restdto.response.ItemCategoryResponseDTO;

import java.util.List;

public interface ItemCategoryService {
    ItemCategoryResponseDTO createCategory(CreateItemCategoryRequestDTO createCategoryRequestDTO, String authHeader);
    List<ItemCategoryResponseDTO> getAllCategory();
    ItemCategoryResponseDTO getCategoryByName(String categoryName);
}
