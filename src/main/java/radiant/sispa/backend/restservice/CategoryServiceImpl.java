package radiant.sispa.backend.restservice;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Category;
import radiant.sispa.backend.repository.CategoryDb;
import radiant.sispa.backend.restdto.request.CreateCategoryRequestDTO;
import radiant.sispa.backend.restdto.response.CategoryResponseDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryDb categoryDb;

    @Autowired
    private JwtUtils jwtUtils;
    
    @Override
    public List<CategoryResponseDTO> getAllCategory() {
        List<Category> categories = categoryDb.findAllByDeletedAtIsNull();

        List<CategoryResponseDTO> listCategoryResponseDTO = new ArrayList<>();
        for (Category category : categories) {
            var categoryResponseDTO = categoryToCategoryResponseDTO(category);
            listCategoryResponseDTO.add(categoryResponseDTO);
        }
        return listCategoryResponseDTO;
    }

    @Override
    public CategoryResponseDTO createCategory(CreateCategoryRequestDTO categoryDTO, String authHeader) throws IllegalArgumentException {
        String token = authHeader.substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        if (categoryDb.findAllByDeletedAtIsNull() != null) {
            Category existingCategory = categoryDb.findByNameIgnoreCaseAndDeletedAtNull(categoryDTO.getName().trim());

            if (existingCategory != null) {
                throw new IllegalArgumentException("Kategori dengan nama ini sudah terdaftar.");
            }
        }

        Category newCategory = new Category();
        newCategory.setName(categoryDTO.getName().trim());
        newCategory.setCreatedBy(username);

        categoryDb.save(newCategory);
        return categoryToCategoryResponseDTO(newCategory);
    }

    private CategoryResponseDTO categoryToCategoryResponseDTO(Category category) {
        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();

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
    public CategoryResponseDTO getCategoryByName(String name) {
        Category category = categoryDb.findByNameIgnoreCaseAndDeletedAtNull(name);

        if (category == null || category.getDeletedAt() != null) {
            return null;
        }

        return categoryToCategoryResponseDTO(category);
    }

}