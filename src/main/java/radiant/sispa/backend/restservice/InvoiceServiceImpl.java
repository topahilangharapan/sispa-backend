package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityNotFoundException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Invoice;
import radiant.sispa.backend.model.PurchaseOrder;
import radiant.sispa.backend.model.PurchaseOrderItem;
import radiant.sispa.backend.repository.InvoiceDb;
import radiant.sispa.backend.repository.PurchaseOrderDb;
import radiant.sispa.backend.restdto.request.CreateInvoiceRequestDTO;
import radiant.sispa.backend.restdto.response.CreateInvoiceResponseDTO;
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
public class InvoiceServiceImpl implements InvoiceService {
    @Autowired
    private InvoiceDb invoiceDb;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PurchaseOrderDb purchaseOrderDb;

    private static final String[] SATUAN = {"", "Satu", "Dua", "Tiga", "Empat", "Lima", "Enam", "Tujuh", "Delapan", "Sembilan"};
    private static final String[] BELASAN = {"Sepuluh", "Sebelas", "Dua Belas", "Tiga Belas", "Empat Belas", "Lima Belas", "Enam Belas", "Tujuh Belas", "Delapan Belas", "Sembilan Belas"};
    private static final String[] PULUHAN = {"", "", "Dua Puluh", "Tiga Puluh", "Empat Puluh", "Lima Puluh", "Enam Puluh", "Tujuh Puluh", "Delapan Puluh", "Sembilan Puluh"};
    private static final String[] RIBUAN = {"", "Ribu", "Juta", "Miliar", "Triliun"};

    @Override
    public CreateInvoiceResponseDTO generatePdfReport(CreateInvoiceRequestDTO createInvoiceRequestDTO, String authHeader) {
        try {
            String token = authHeader.substring(7);
            String createdBy = jwtUtils.getUserNameFromJwtToken(token);

            PurchaseOrder purchaseOrder = purchaseOrderDb.findPurchaseOrderById(createInvoiceRequestDTO.getPurchaseOrderId());
            if (purchaseOrder == null) {
                throw new FileNotFoundException("Purchase Order tidak ditemukan.");
            }

            Invoice invoice = createInvoiceRequestDTOToInvoice(createInvoiceRequestDTO, purchaseOrder, createdBy);


            InputStream reportStream = new ClassPathResource("/static/report/invoice.jrxml").getInputStream();
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

                data.add(row);
            }

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("logoSPA", "static/images/logo_spa_with_text.png");
            parameters.put("perusahaan", purchaseOrder.getCompanyName());
            parameters.put("alamatPerusahaan", purchaseOrder.getCompanyAddress());
            parameters.put("penerima", invoice.getReceiver());
            parameters.put("tanggalDibuat", invoice.getDateCreated());
            parameters.put("noInvoice", invoice.getNoInvoice());
            parameters.put("noPo", purchaseOrder.getNoPo());
            parameters.put("tanggalPembayaran", invoice.getDatePaid());
            parameters.put("subTotal", formatWithThousandSeparator(invoice.getSubTotal()));
            parameters.put("ppnPersen", String.valueOf(Math.round(invoice.getPpnPercentage() * 100)));
            parameters.put("ppn", formatWithThousandSeparator(invoice.getPpn()));
            parameters.put("total", formatWithThousandSeparator(invoice.getTotal()));
            parameters.put("terbilang", invoice.getSpelledOut());
            parameters.put("namaBank", invoice.getBankName());
            parameters.put("noRek", invoice.getAccountNumber());
            parameters.put("atasNama", invoice.getOnBehalf());
            parameters.put("tempat", invoice.getPlaceSigned());
            parameters.put("tanggalDitandatangani", invoice.getDateSigned());
            parameters.put("tandaTangan", "static/images/ttd_po.jpg");
            parameters.put("yangMenandatangani", invoice.getSignee());
            parameters.put("footerSpa", "static/images/footer_spa.jpg");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            CreateInvoiceResponseDTO createInvoiceResponseDTO = new CreateInvoiceResponseDTO();
            createInvoiceResponseDTO.setPdf(pdfBytes);
            createInvoiceResponseDTO.setFileName(invoice.getFileName());

            return createInvoiceResponseDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating PDF report", e.getCause());
        }
    }

    private Invoice createInvoiceRequestDTOToInvoice(CreateInvoiceRequestDTO createInvoiceRequestDTO, PurchaseOrder purchaseOrder, String createdBy) {
        Invoice invoice = new Invoice();

        invoice.setCreatedBy(createdBy);
        invoice.setReceiver(createInvoiceRequestDTO.getReceiver());
        invoice.setDateCreated(createInvoiceRequestDTO.getDateCreated());
        invoice.setNoInvoice(createNoInvoice(createInvoiceRequestDTO, purchaseOrder));
        invoice.setNoPo(purchaseOrder.getNoPo());
        invoice.setDatePaid(createInvoiceRequestDTO.getDatePaid());
        invoice.setPurchaseOrder(purchaseOrder);
        invoice.setSubTotal(purchaseOrder.getTotal());
        invoice.setPpnPercentage(Double.parseDouble(createInvoiceRequestDTO.getPpnPercentage()) / 100);
        invoice.setPpn(Math.round(invoice.getSubTotal() * invoice.getPpnPercentage()));
        invoice.setTotal(invoice.getSubTotal() + invoice.getPpn());
        invoice.setSpelledOut(spellItOut(invoice.getTotal()));
        invoice.setBankName(createInvoiceRequestDTO.getBankName());
        invoice.setAccountNumber(createInvoiceRequestDTO.getAccountNumber());
        invoice.setOnBehalf(createInvoiceRequestDTO.getOnBehalf());
        invoice.setPlaceSigned(createInvoiceRequestDTO.getPlaceSigned());
        invoice.setDateSigned(createInvoiceRequestDTO.getDateSigned());
        invoice.setSignee(createInvoiceRequestDTO.getSignee());
        invoice.setEvent(createInvoiceRequestDTO.getEvent());
        invoice.setFileName(createFileName(invoice));

        return invoiceDb.save(invoice);
    }

    private String createNoInvoice(CreateInvoiceRequestDTO createInvoiceRequestDTO, PurchaseOrder purchaseOrder) {
        String companyAbbreviation = getAbbreviation(purchaseOrder.getCompanyName());

        LocalDate today = LocalDate.now();
        Instant startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        List<Invoice> invoicesToday = invoiceDb.findInvoicesToday(startOfDay, endOfDay);
        String numberOfInvoicesToday = String.format("%03d", invoicesToday.size());

        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("ddMMyy");
        String formattedDate1 = today.format(formatter1);

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
        String formattedDate2 = today.format(formatter2);


        return String.format("I/%s/%s/%s", numberOfInvoicesToday, companyAbbreviation, formattedDate1);
    }

    private String createFileName(Invoice invoice) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
        String formattedDate2 = today.format(formatter2);


        return String.format("%s - %s - %s %s", invoice.getNoInvoice(), invoice.getPurchaseOrder().getCompanyName(), invoice.getEvent(), formattedDate2);
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
    public List<InvoiceResponseDTO> getAllInvoices() {
        List<Invoice> invoices = invoiceDb.findAllByDeletedAtIsNullAndPurchaseOrderDeletedAtIsNull();

        List<InvoiceResponseDTO> result = new ArrayList<>();
        for (Invoice inv : invoices) {
            result.add(convertToResponse(inv));
        }
        return result;
    }

    @Override
    public InvoiceResponseDTO getInvoiceById(Long id) {
        Invoice invoice = invoiceDb.findByIdAndDeletedAtNull(id);
        return convertToResponse(invoice);
    }

    @Override
    public void deleteInvoice(Long id) throws EntityNotFoundException {
        Invoice invoiceToDelete = invoiceDb.findByIdAndDeletedAtNull(id);

        if (invoiceToDelete == null) {
            throw new EntityNotFoundException("Invoice not found");
        }

        invoiceToDelete.setDeletedAt(new Date().toInstant());
        invoiceDb.save(invoiceToDelete);
    }

    private InvoiceResponseDTO convertToResponse(Invoice entity) {
        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setReceiver(entity.getReceiver());
        dto.setDateCreated(entity.getDateCreated());
        dto.setNoInvoice(entity.getNoInvoice());
        dto.setNoPo(entity.getNoPo());
        dto.setDatePaid(entity.getDatePaid());
        dto.setSubTotal(entity.getSubTotal());
        dto.setPpnPercentage(entity.getPpnPercentage());
        dto.setPpn(entity.getPpn());
        dto.setTotal(entity.getTotal());
        dto.setSpelledOut(entity.getSpelledOut());
        dto.setBankName(entity.getBankName());
        dto.setAccountNumber(entity.getAccountNumber());
        dto.setOnBehalf(entity.getOnBehalf());
        dto.setPlaceSigned(entity.getPlaceSigned());
        dto.setDateSigned(entity.getDateSigned());
        dto.setSignee(entity.getSignee());
        dto.setEvent(entity.getEvent());
        dto.setFileName(entity.getFileName());

        // Convert items
        List<PurchaseOrderItemResponseDTO> itemDTOs = new ArrayList<>();
        PurchaseOrder po = purchaseOrderDb.findPurchaseOrderByNoPo(entity.getNoPo());
        if (po.getItems() != null) {
            for (PurchaseOrderItem item : po.getItems()) {
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

    @Override
    public CreateInvoiceResponseDTO generatePdfByInvoiceId(Long invoiceId, String authHeader) {
        Invoice invoice = invoiceDb.findByIdAndDeletedAtNull(invoiceId);
        if (invoice == null) {
            throw new EntityNotFoundException("Invoice tidak ditemukan.");
        }

        try {
            PurchaseOrder purchaseOrder = invoice.getPurchaseOrder();

            InputStream reportStream = new ClassPathResource("/static/report/invoice.jrxml").getInputStream();
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

                data.add(row);
            }

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("logoSPA", "static/images/logo_spa_with_text.png");
            parameters.put("perusahaan", purchaseOrder.getCompanyName());
            parameters.put("alamatPerusahaan", purchaseOrder.getCompanyAddress());
            parameters.put("penerima", invoice.getReceiver());
            parameters.put("tanggalDibuat", invoice.getDateCreated());
            parameters.put("noInvoice", invoice.getNoInvoice());
            parameters.put("noPo", purchaseOrder.getNoPo());
            parameters.put("tanggalPembayaran", invoice.getDatePaid());
            parameters.put("subTotal", formatWithThousandSeparator(invoice.getSubTotal()));
            parameters.put("ppnPersen", String.valueOf(Math.round(invoice.getPpnPercentage() * 100)));
            parameters.put("ppn", formatWithThousandSeparator(invoice.getPpn()));
            parameters.put("total", formatWithThousandSeparator(invoice.getTotal()));
            parameters.put("terbilang", invoice.getSpelledOut());
            parameters.put("namaBank", invoice.getBankName());
            parameters.put("noRek", invoice.getAccountNumber());
            parameters.put("atasNama", invoice.getOnBehalf());
            parameters.put("tempat", invoice.getPlaceSigned());
            parameters.put("tanggalDitandatangani", invoice.getDateSigned());
            parameters.put("tandaTangan", "static/images/ttd_po.jpg");
            parameters.put("yangMenandatangani", invoice.getSignee());
            parameters.put("footerSpa", "static/images/footer_spa.jpg");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            CreateInvoiceResponseDTO createInvoiceResponseDTO = new CreateInvoiceResponseDTO();
            createInvoiceResponseDTO.setPdf(pdfBytes);
            createInvoiceResponseDTO.setFileName(invoice.getFileName());

            return createInvoiceResponseDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating PDF report", e.getCause());
        }
    }

}
