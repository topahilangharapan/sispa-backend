package radiant.sispa.backend.restservice;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.PurchaseOrder;
import radiant.sispa.backend.model.PurchaseOrderItem;
import radiant.sispa.backend.repository.PurchaseOrderDb;
import radiant.sispa.backend.repository.PurchaseOrderItemDb;
import radiant.sispa.backend.restdto.request.CreatePurchaseOrderRequestDTO;
import radiant.sispa.backend.restdto.response.CreatePurchaseOrderResponseDTO;
import radiant.sispa.backend.restdto.response.PurchaseOrderItemResponseDTO;
import radiant.sispa.backend.restdto.response.PurchaseOrderResponseDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.io.InputStream;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    @Autowired
    private PurchaseOrderDb purchaseOrderDb;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PurchaseOrderItemDb purchaseOrderItemDb;

    private static final String[] SATUAN = {"", "Satu", "Dua", "Tiga", "Empat", "Lima", "Enam", "Tujuh", "Delapan", "Sembilan"};
    private static final String[] BELASAN = {"Sepuluh", "Sebelas", "Dua Belas", "Tiga Belas", "Empat Belas", "Lima Belas", "Enam Belas", "Tujuh Belas", "Delapan Belas", "Sembilan Belas"};
    private static final String[] PULUHAN = {"", "", "Dua Puluh", "Tiga Puluh", "Empat Puluh", "Lima Puluh", "Enam Puluh", "Tujuh Puluh", "Delapan Puluh", "Sembilan Puluh"};
    private static final String[] RIBUAN = {"", "Ribu", "Juta", "Miliar", "Triliun"};

    @Override
    public CreatePurchaseOrderResponseDTO generatePdfReport(CreatePurchaseOrderRequestDTO createPurchaseOrderRequestDTO, String authHeader) {
        try {
            String token = authHeader.substring(7);
            String createdBy = jwtUtils.getUserNameFromJwtToken(token);

            PurchaseOrder purchaseOrder = createPurchaseOrderToPurchaseOrder(createPurchaseOrderRequestDTO, createdBy);

            InputStream reportStream = new ClassPathResource("/static/report/purchase-order.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            List<Map<String, Object>> data = new ArrayList<>();

            Long count = 1L;
            for (PurchaseOrderItem purchaseOrderItem : purchaseOrder.getItems()) {
                Map<String, Object> row = new HashMap<>();

                row.put("no", String.valueOf(count++));
                row.put("uraianJudul", purchaseOrderItem.getTitle());
                row.put("volume", formatWithThousandSeparator(purchaseOrderItem.getVolume()));
                row.put("satuan", purchaseOrderItem.getUnit());
                row.put("hargaSatuan", formatWithThousandSeparator(purchaseOrderItem.getPricePerUnit()));
                row.put("jumlah", formatWithThousandSeparator(purchaseOrderItem.getSum()));
                row.put("blank", "");
                row.put("uraianDeskripsi", purchaseOrderItem.getDescription());

                data.add(row);
            }

            if (purchaseOrder.getItems().isEmpty()) {
                Map<String, Object> row = new HashMap<>();

                row.put("no", "");
                row.put("uraianJudul","");
                row.put("volume", "");
                row.put("satuan", "");
                row.put("hargaSatuan", "");
                row.put("jumlah", "");
                row.put("blank", "");
                row.put("uraianDeskripsi", "");

                data.add(row);
            }

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("logoSPA", "static/images/logo_spa_with_text.png");
            parameters.put("perusahaan", purchaseOrder.getCompanyName());
            parameters.put("alamatPerusahaan", purchaseOrder.getCompanyAddress());
            parameters.put("penerima", purchaseOrder.getReceiver());
            parameters.put("tanggalDibuat", purchaseOrder.getDateCreated());
            parameters.put("noPo", purchaseOrder.getNoPo());
            parameters.put("total", formatWithThousandSeparator(purchaseOrder.getTotal()));
            parameters.put("terbilang", purchaseOrder.getSpelledOut());
            parameters.put("ketentuan", purchaseOrder.getTerms());
            parameters.put("tempat", purchaseOrder.getPlaceSigned());
            parameters.put("tanggalDitandatangani", purchaseOrder.getDateSigned());
            parameters.put("tandaTangan", "static/images/ttd_po.jpg");
            parameters.put("yangMenandatangani", purchaseOrder.getSignee());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);


            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            CreatePurchaseOrderResponseDTO createPurchaseOrderResponseDTO = new CreatePurchaseOrderResponseDTO();
            createPurchaseOrderResponseDTO.setPdf(pdfBytes);
            createPurchaseOrderResponseDTO.setFileName(purchaseOrder.getFileName());

            return createPurchaseOrderResponseDTO;

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e.getCause());
        }
    }

    private PurchaseOrder createPurchaseOrderToPurchaseOrder(CreatePurchaseOrderRequestDTO createPurchaseOrderRequestDTO, String createdBy) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        List<PurchaseOrderItem> items = savePurchaseOrderItem(createPurchaseOrderRequestDTO.getItems(), createdBy);

        purchaseOrder.setCreatedBy(createdBy);
        purchaseOrder.setCompanyName(createPurchaseOrderRequestDTO.getCompanyName());
        purchaseOrder.setCompanyAddress(createPurchaseOrderRequestDTO.getCompanyAddress());
        purchaseOrder.setReceiver(createPurchaseOrderRequestDTO.getReceiver());
        purchaseOrder.setDateCreated(createPurchaseOrderRequestDTO.getDateCreated());
        purchaseOrder.setNoPo(createNoPo(createPurchaseOrderRequestDTO));
        purchaseOrder.setItems(items);

        Long total = 0L;
        for (PurchaseOrderItem item : items) {
            total += item.getSum();
        }

        purchaseOrder.setTotal(total);
        purchaseOrder.setSpelledOut(spellItOut(total));
        purchaseOrder.setTerms(createPurchaseOrderRequestDTO.getTerms());
        purchaseOrder.setPlaceSigned(createPurchaseOrderRequestDTO.getPlaceSigned());
        purchaseOrder.setDateSigned(createPurchaseOrderRequestDTO.getDateSigned());
        purchaseOrder.setSignee(createPurchaseOrderRequestDTO.getSignee());
        purchaseOrder.setFileName(createFileName(purchaseOrder));

        return purchaseOrderDb.save(purchaseOrder);
    }

    private List<PurchaseOrderItem> savePurchaseOrderItem(List<Map<String, String>> items, String createdBy) {
        List<PurchaseOrderItem> result = new ArrayList<>();

        for (Map<String, String> item : items) {
            PurchaseOrderItem purchaseOrderItem = new PurchaseOrderItem();

            purchaseOrderItem.setCreatedBy(createdBy);
            purchaseOrderItem.setTitle(item.get("title"));
            purchaseOrderItem.setVolume(Long.parseLong(item.get("volume")));
            purchaseOrderItem.setUnit(item.get("unit"));
            purchaseOrderItem.setPricePerUnit(Long.parseLong(item.get("pricePerUnit")));
            purchaseOrderItem.setSum(purchaseOrderItem.getVolume() * purchaseOrderItem.getPricePerUnit());
            purchaseOrderItem.setDescription(item.get("description"));

            result.add(purchaseOrderItemDb.save(purchaseOrderItem));
        }

        return result;
    }

    private String createNoPo(CreatePurchaseOrderRequestDTO createPurchaseOrderRequestDTO) {
        String companyAbbreviation = getAbbreviation(createPurchaseOrderRequestDTO.getCompanyName());

        LocalDate today = LocalDate.now();
        Instant startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        List<PurchaseOrder> purchaseOrdersToday = purchaseOrderDb.findPurchaseOrdersToday(startOfDay, endOfDay);
        String numberOfPurchaseOrdersToday = String.format("%03d", purchaseOrdersToday.size());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");
        String formattedDate = today.format(formatter);

        return String.format("PO/%s/%s/%s", numberOfPurchaseOrdersToday, companyAbbreviation, formattedDate);
    }

    private String createFileName(PurchaseOrder purchaseOrder) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");
        String formattedDate = today.format(formatter);

        return String.format("%s_%s", purchaseOrder.getNoPo(), formattedDate);
    }

    public String getAbbreviation(String companyName) {
        if (companyName == null || companyName.isEmpty()) {
            return "";
        }

        String[] words = companyName.split(" ");
        StringBuilder abbreviation = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                abbreviation.append(Character.toUpperCase(word.charAt(0)));
            }
        }

        return abbreviation.toString();
    }

    private static String spellItOut(long number) {
        if (number == 0) return "Nol Rupiah";

        Stack<String> stack = new Stack<>();
        int groupIndex = 0;

        while (number > 0) {
            int threeDigits = (int) (number % 1000);
            if (threeDigits > 0) {
                stack.push(convertThreeDigits(threeDigits) + " " + RIBUAN[groupIndex]);
            }
            number /= 1000;
            groupIndex++;
        }

        StringBuilder result = new StringBuilder();
        while (!stack.isEmpty()) {
            result.append(stack.pop()).append(" ");
        }

        return result.toString().trim() + " Rupiah";
    }

    private static String convertThreeDigits(int num) {
        String result = "";
        int ratusan = num / 100;
        int puluhan = (num % 100) / 10;
        int satuan = num % 10;

        if (ratusan > 0) {
            result += (ratusan == 1 ? "Seratus" : SATUAN[ratusan] + " Ratus");
        }
        if (puluhan == 1) {
            result += " " + BELASAN[satuan];
        } else {
            if (puluhan > 1) result += " " + PULUHAN[puluhan];
            if (satuan > 0) result += " " + SATUAN[satuan];
        }

        return result.trim();
    }

    private static String formatWithThousandSeparator(long number) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        return formatter.format(number);
    }


    @Override
    public List<PurchaseOrderResponseDTO> getAllPurchaseOrders() {
        List<PurchaseOrder> purchaseOrders = purchaseOrderDb.findByDeletedAtIsNull();
        
        // Convert each to a response DTO
        List<PurchaseOrderResponseDTO> result = new ArrayList<>();
        for (PurchaseOrder po : purchaseOrders) {
            result.add(convertToResponse(po));
        }
        return result;
    }

    @Override
    public PurchaseOrderResponseDTO getPurchaseOrderById(Long id) {
        PurchaseOrder po = purchaseOrderDb.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Purchase order not found with id: " + id));
        return convertToResponse(po);
    }

    public void deletePurchaseOrder(Long id) throws NoSuchElementException {
        Optional<PurchaseOrder> purchaseOrderOptional = purchaseOrderDb.findById(id);
        if (!purchaseOrderOptional.isPresent()) {
            throw new NoSuchElementException("Purchase order not found");
        }
        
        PurchaseOrder purchaseOrder = purchaseOrderOptional.get();
        purchaseOrder.setDeletedAt(new Date());
        purchaseOrderDb.save(purchaseOrder); 
    }

    /**
     * Helper method to convert a PurchaseOrder entity into a PurchaseOrderResponseDTO
     */
    private PurchaseOrderResponseDTO convertToResponse(PurchaseOrder entity) {
        PurchaseOrderResponseDTO dto = new PurchaseOrderResponseDTO();
        dto.setId(entity.getId());
        dto.setCompanyName(entity.getCompanyName());
        dto.setCompanyAddress(entity.getCompanyAddress());
        dto.setReceiver(entity.getReceiver());
        dto.setNoPo(entity.getNoPo());
        dto.setDateCreated(entity.getDateCreated());
        dto.setTotal(entity.getTotal());
        dto.setSpelledOut(entity.getSpelledOut());
        dto.setTerms(entity.getTerms());
        dto.setPlaceSigned(entity.getPlaceSigned());
        dto.setDateSigned(entity.getDateSigned());
        dto.setSignee(entity.getSignee());

        // Convert items
        List<PurchaseOrderItemResponseDTO> itemDTOs = new ArrayList<>();
        if (entity.getItems() != null) {
            for (PurchaseOrderItem item : entity.getItems()) {
                PurchaseOrderItemResponseDTO itemDTO = new PurchaseOrderItemResponseDTO();
                itemDTO.setId(item.getId());
                itemDTO.setTitle(item.getTitle());
                itemDTO.setVolume(item.getVolume());
                itemDTO.setUnit(item.getUnit());
                itemDTO.setPricePerUnit(item.getPricePerUnit());
                itemDTO.setSum(item.getSum());
                itemDTO.setDescription(item.getDescription());
                itemDTOs.add(itemDTO);
            }
        }
        dto.setItems(itemDTOs);

        return dto;
    }

}
