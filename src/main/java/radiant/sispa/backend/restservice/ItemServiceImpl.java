package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Invoice;
import radiant.sispa.backend.model.Item;
import radiant.sispa.backend.model.PurchaseOrder;
import radiant.sispa.backend.repository.InvoiceDb;
import radiant.sispa.backend.repository.ItemDb;
import radiant.sispa.backend.repository.PurchaseOrderDb;
import radiant.sispa.backend.restdto.request.CreateInvoiceRequestDTO;
import radiant.sispa.backend.restdto.request.CreateItemRequestDTO;
import radiant.sispa.backend.restdto.response.CreateInvoiceResponseDTO;
import radiant.sispa.backend.restdto.response.CreateItemResponseDTO;
import radiant.sispa.backend.restdto.response.InvoiceResponseDTO;
import radiant.sispa.backend.restdto.response.PurchaseOrderItemResponseDTO;
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

        return createItemResponseDTO;
    }

    private Item createItemRequestDTOToItem(CreateItemRequestDTO createItemRequestDTO, String createdBy) {
        Item item = new Item();

        item.setCreatedBy(createdBy);
        item.setTitle(createItemRequestDTO.getTitle());
        item.setUnit(createItemRequestDTO.getUnit());
        item.setPricePerUnit(createItemRequestDTO.getPricePerUnit());
        item.setDescription(createItemRequestDTO.getDescription());

        return item;
    }

}
