package radiant.sispa.backend.restservice;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.ItemCategory;
import radiant.sispa.backend.repository.ItemCategoryDb;
import radiant.sispa.backend.restdto.request.CreateItemCategoryRequestDTO;
import radiant.sispa.backend.restdto.response.ItemCategoryResponseDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ItemCategoryServiceImpl implements ItemCategoryService {
    @Autowired
    private ItemCategoryDb categoryDb;

    @Autowired
    private JwtUtils jwtUtils;
    
    @Override
    public List<ItemCategoryResponseDTO> getAllCategory() {
        List<ItemCategory> categories = categoryDb.findAllByDeletedAtIsNull();

        List<ItemCategoryResponseDTO> listCategoryResponseDTO = new ArrayList<>();
        for (ItemCategory category : categories) {
            var categoryResponseDTO = categoryToCategoryResponseDTO(category);
            listCategoryResponseDTO.add(categoryResponseDTO);
        }
        return listCategoryResponseDTO;
    }

    @Override
    public ItemCategoryResponseDTO createCategory(CreateItemCategoryRequestDTO categoryDTO, String authHeader) throws IllegalArgumentException {
        String token = authHeader.substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        if (categoryDb.findAllByDeletedAtIsNull() != null) {
            ItemCategory existingCategory = categoryDb.findByNameIgnoreCaseAndDeletedAtNull(categoryDTO.getName().trim());

            if (existingCategory != null) {
                throw new IllegalArgumentException("Kategori dengan nama ini sudah terdaftar.");
            }
        }

        ItemCategory newCategory = new ItemCategory();
        newCategory.setName(categoryDTO.getName().trim());
        newCategory.setCreatedBy(username);

        categoryDb.save(newCategory);
        return categoryToCategoryResponseDTO(newCategory);
    }

    private ItemCategoryResponseDTO categoryToCategoryResponseDTO(ItemCategory category) {
        ItemCategoryResponseDTO categoryResponseDTO = new ItemCategoryResponseDTO();

        categoryResponseDTO.setId(category.getId());
        categoryResponseDTO.setName(category.getName());
        categoryResponseDTO.setCreatedAt(category.getCreatedAt());
        categoryResponseDTO.setUpdatedAt(category.getUpdatedAt());
        categoryResponseDTO.setCreatedBy(category.getCreatedBy());
        categoryResponseDTO.setUpdatedBy(category.getUpdatedBy());
        categoryResponseDTO.setDeletedAt(category.getDeletedAt());
        return categoryResponseDTO;
    }

    @Override
    public ItemCategoryResponseDTO getCategoryByName(String name) {
        ItemCategory category = categoryDb.findByNameIgnoreCaseAndDeletedAtNull(name);

        if (category == null || category.getDeletedAt() != null) {
            return null;
        }

        return categoryToCategoryResponseDTO(category);
    }

}