package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Category;
import radiant.sispa.backend.model.Invoice;
import radiant.sispa.backend.model.Item;
import radiant.sispa.backend.model.PurchaseOrder;
import radiant.sispa.backend.repository.InvoiceDb;
import radiant.sispa.backend.repository.ItemDb;
import radiant.sispa.backend.repository.PurchaseOrderDb;
import radiant.sispa.backend.restdto.request.CreateInvoiceRequestDTO;
import radiant.sispa.backend.restdto.request.CreateItemRequestDTO;
import radiant.sispa.backend.restdto.response.*;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemDb itemDb;

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

    @Override
    public List<ItemResponseDTO> getAllItems() {
        List<Item> items = itemDb.findByDeletedAtNull();
        List<ItemResponseDTO> result = new ArrayList<>();

        for (Item item : items) {
            result.add(itemToItemResponseDTO(item));
        }

        return result;
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

        return itemResponseDTO;
    }

    @Override
    public Item getItemById(Long id) {
        return itemDb.findById(id).orElseThrow(EntityNotFoundException::new);
    }

}
