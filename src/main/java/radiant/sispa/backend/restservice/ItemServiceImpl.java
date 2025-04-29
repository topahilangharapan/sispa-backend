package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.*;
import radiant.sispa.backend.repository.ItemDb;
import radiant.sispa.backend.repository.ItemStatusDb;
import radiant.sispa.backend.restdto.request.CreateItemRequestDTO;
import radiant.sispa.backend.restdto.request.UpdateItemRequestRestDTO;
import radiant.sispa.backend.restdto.request.UpdateItemStatusRequestRestDTO;
import radiant.sispa.backend.restdto.response.*;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.time.Instant;

import java.util.*;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemDb itemDb;

    @Autowired
    private ItemStatusDb itemStatusDb;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemStatusService itemStatusService;

    @Override
    public CreateItemResponseDTO createItem(CreateItemRequestDTO createItemRequestDTO, String authHeader) {
        String token = authHeader.substring(7);
        String createdBy = jwtUtils.getUserNameFromJwtToken(token);

        Item item = itemDb.save(createItemRequestDTOToItem(createItemRequestDTO, createdBy));

        CreateItemResponseDTO createItemResponseDTO = new CreateItemResponseDTO();

        createItemResponseDTO.setTitle(item.getTitle());
        createItemResponseDTO.setDescription(item.getDescription());
        createItemResponseDTO.setUnit(item.getUnit());
        createItemResponseDTO.setPricePerUnit(item.getPricePerUnit());
        createItemResponseDTO.setCategory(item.getCategory().getName());

        return createItemResponseDTO;
    }

    private Item createItemRequestDTOToItem(CreateItemRequestDTO createItemRequestDTO, String createdBy) {
        Item item = new Item();

        item.setCreatedBy(createdBy);
        item.setUpdatedBy(createdBy);
        item.setTitle(createItemRequestDTO.getTitle());
        item.setUnit(createItemRequestDTO.getUnit());
        item.setPricePerUnit(createItemRequestDTO.getPricePerUnit());
        item.setDescription(createItemRequestDTO.getDescription());

        Category category = new Category();
        CategoryResponseDTO categoryResponseDTO = categoryService.getCategoryByName(createItemRequestDTO.getCategory());

        category.setId(categoryResponseDTO.getId());
        category.setName(categoryResponseDTO.getName());

        item.setStatus(itemStatusService.getItemStatusByName("TERSEDIA"));

        item.setCategory(category);

        return item;
    }

    private ItemResponseDTO itemToItemResponseDTO(Item item) {
        ItemResponseDTO itemResponseDTO = new ItemResponseDTO();

        itemResponseDTO.setId(item.getId());
        itemResponseDTO.setTitle(item.getTitle());
        itemResponseDTO.setDescription(item.getDescription());
        itemResponseDTO.setUnit(item.getUnit());
        itemResponseDTO.setPricePerUnit(item.getPricePerUnit());
        itemResponseDTO.setCategory(item.getCategory().getName());
        itemResponseDTO.setStatus(item.getStatus().getStatus());
        itemResponseDTO.setCreatedAt(item.getCreatedAt());
        itemResponseDTO.setCreatedBy(item.getCreatedBy());
        itemResponseDTO.setUpdatedAt(item.getUpdatedAt());
        itemResponseDTO.setUpdatedBy(item.getUpdatedBy());
        itemResponseDTO.setDeletedAt(item.getDeletedAt());

        return itemResponseDTO;
    }

    @Override
    public ItemResponseDTO updateItem(Long id, UpdateItemRequestRestDTO itemDTO, String username) {
        Item item = itemDb.findById(id).orElse(null);
        if (item == null || item.getDeletedAt() != null){
            return null;
        }

        item.setTitle(itemDTO.getTitle());
        item.setDescription(itemDTO.getDescription());
        item.setUnit(itemDTO.getUnit());
        item.setPricePerUnit(itemDTO.getPricePerUnit());
        if (itemDTO.getCategory() != null && !itemDTO.getCategory().equals(item.getCategory().getName())) {
            CategoryResponseDTO categoryResponseDTO = categoryService.getCategoryByName(itemDTO.getCategory());
            Category category = new Category();
            category.setId(categoryResponseDTO.getId());
            category.setName(categoryResponseDTO.getName());
            item.setCategory(category);
        }

        item.setUpdatedAt(Instant.now());
        item.setUpdatedBy(username);

        Item updatedItem = itemDb.save(item);

        return itemToItemResponseDTO(updatedItem);


    }

    @Override
    public Item getItemById(Long id) {
        return itemDb.findById(id).orElseThrow(EntityNotFoundException::new);
    }


    @Override
    public ItemResponseDTO detailItem(Long id) {
        Item item = itemDb.findById(id).orElse(null);
        if (item == null || item.getDeletedAt() != null){
            return null;
        }

        return itemToItemResponseDTO(item);
    }

    @Override
    public List<ItemResponseDTO> getAllItems() {
        List<Item> items = itemDb.findByDeletedAtNull();
        List<ItemResponseDTO> result = new ArrayList<>();

        for (Item item : items) {
            result.add(itemToItemResponseDTO(item));
        }

        return result;
    }

    @Override
    public void deleteItem(Long id) throws EntityNotFoundException {
        Item itemToDelete = itemDb.findByIdAndDeletedAtNull(id);
        List<PurchaseOrderItem> listPurchaseOrderItems = itemToDelete.getPurchaseOrderItems();

        for (PurchaseOrderItem purchaseOrderItem : listPurchaseOrderItems) {
            System.out.println("id dari PO " + purchaseOrderItem.getItem().getId());
            System.out.println("id " + id);
            if (purchaseOrderItem.getItem().getId().equals(id)) {
                throw new IllegalStateException("Item tidak dapat dihapus karena memiliki PO.");
            }
        }

        itemToDelete.setDeletedAt(new Date().toInstant());
        itemDb.save(itemToDelete);
    }

    @Override
    public ItemResponseDTO updateItemStatus(Long id, Long statusId, UpdateItemStatusRequestRestDTO itemDTO, String username) {
        Item item = itemDb.findById(id).orElse(null);
        if (item == null || item.getDeletedAt() != null){
            return null;
        }

        ItemStatus status = itemStatusDb.findById(statusId).orElseThrow(() -> new EntityNotFoundException("Status not found"));

        item.setStatus(status);
        item.setUpdatedAt(Instant.now());
        item.setUpdatedBy(username);

        Item updatedItem = itemDb.save(item);

        return itemToItemResponseDTO(updatedItem);
    }
}