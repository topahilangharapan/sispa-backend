package radiant.sispa.backend.restservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Base64;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import radiant.sispa.backend.model.*;
import radiant.sispa.backend.repository.FinalReportDb;
import radiant.sispa.backend.repository.ImageDb;
import radiant.sispa.backend.repository.InvoiceDb;
import radiant.sispa.backend.repository.PurchaseOrderDb;
import radiant.sispa.backend.restdto.request.CreateFinalReportRequestDTO;
import radiant.sispa.backend.restdto.request.CreateInvoiceRequestDTO;
import radiant.sispa.backend.restdto.response.*;
import radiant.sispa.backend.restdto.response.*;
import radiant.sispa.backend.security.jwt.JwtUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class FinalReportServiceImpl implements FinalReportService {
    @Autowired
    private ImageDb imageDb;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private FinalReportDb finalReportDb;

    @Override
    public CreateFinalReportRequestDTO convertToCreateFinalReportRequestDTO(String rawData, List<MultipartFile> images, String createdBy) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(rawData);
        CreateFinalReportRequestDTO createFinalReportRequestDTO = objectMapper.treeToValue(jsonNode, CreateFinalReportRequestDTO.class);

        List<Image> imageList = saveImages(images, createdBy);

        List<Long> imageIdList = new ArrayList<>();
        for (Image image : imageList) {
            imageIdList.add(image.getId());
        }

        createFinalReportRequestDTO.setImageListId(imageIdList);
        return createFinalReportRequestDTO;
    }

    @Override
    public CreateFinalReportResponseDTO generatePdfReport(CreateFinalReportRequestDTO createFinalReportRequestDTO, String authHeader) {
        try {

            String token = authHeader.substring(7);
            String createdBy = jwtUtils.getUserNameFromJwtToken(token);

            FinalReport finalReport = createFinalReportRequestToFinalReport(createFinalReportRequestDTO, createdBy);

            InputStream reportStream = new ClassPathResource("/static/report/final-report.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            List<Map<String, Object>> data = new ArrayList<>();

            Long count = 0L;
            Map<String, Object> row = new HashMap<>();
            for (Image image : finalReport.getImages()) {


                if (count % 2 == 0) {
                    row.put("imageLeft", new ByteArrayInputStream(image.getFileData()));
                } else {
                    row.put("imageRight", new ByteArrayInputStream(image.getFileData()));
                }

                if (row.size() == 2){
                    data.add(row);
                    row = new HashMap<>();
                }

                else if (count + 1 == finalReport.getImages().size()) {
                    data.add(row);
                }

                count++;
            }

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("logoSPA", "static/images/logo_spa_with_text.png");
            parameters.put("event", finalReport.getEvent());
            parameters.put("tanggal", finalReport.getEventDate());
            parameters.put("perusahaan", finalReport.getCompany());
            parameters.put("footerSpa", "static/images/footer_spa.jpg");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            CreateFinalReportResponseDTO createFinalReportResponseDTO = new CreateFinalReportResponseDTO();
            createFinalReportResponseDTO.setPdf(pdfBytes);
            createFinalReportResponseDTO.setFileName(finalReport.getFileName());

            return createFinalReportResponseDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating PDF report", e.getCause());
        }
    }

    private List<Image> saveImages(List<MultipartFile> images, String createdBy) throws IOException {
        List<Image> imageList = new ArrayList<>();

        for (MultipartFile image : images) {
            Image img = new Image();

            img.setCreatedBy(createdBy);
            img.setFileName(image.getOriginalFilename());
            img.setFileData(image.getBytes());

            imageList.add(imageDb.save(img));
        }

        return imageList;
    }

    private List<Image> getImages(List<Long> imagesId)  {
        List<Image> imageList = new ArrayList<>();

        for (Long id: imagesId) {
            imageList.add(imageDb.findById(id));
        }

        return imageList;
    }

    private FinalReport createFinalReportRequestToFinalReport(CreateFinalReportRequestDTO createFinalReportRequestDTO, String createdBy) throws IOException {
        FinalReport finalReport = new FinalReport();

        finalReport.setCreatedBy(createdBy);
        finalReport.setImages(getImages(createFinalReportRequestDTO.getImageListId()));
        finalReport.setEvent(createFinalReportRequestDTO.getEvent());
        finalReport.setCompany(createFinalReportRequestDTO.getPerusahaan());
        finalReport.setEventDate(createFinalReportRequestDTO.getTanggal());
        finalReport.setFileName(createFileName(finalReport));

        return finalReportDb.save(finalReport);
    }


    private String createFileName(FinalReport finalReport) {
        return String.format("Final Report %s", finalReport.getEvent());
    }

    @Override
    public List<FinalReportResponseDTO> getAllFinalReports() {
        List<FinalReport> finalReports = finalReportDb.findByDeletedAtNull();

        List<FinalReportResponseDTO> result = new ArrayList<>();
        for (FinalReport report : finalReports) {
            result.add(convertToResponse(report));
        }
        return result;
    }

    @Override
    public FinalReportResponseDTO getReportsById(Long id) {
        FinalReport report = finalReportDb.findById(id);
        return convertToResponse(report);
    }

    private FinalReportResponseDTO convertToResponse(FinalReport entity) {
        FinalReportResponseDTO dto = new FinalReportResponseDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setFileName(entity.getFileName());
        dto.setEvent(entity.getEvent());
        dto.setEventDate(entity.getEventDate());
        dto.setCompany(entity.getCompany());

        // Convert items
        List<ImageResponseDTO> itemDTOs = new ArrayList<>();
        if (entity.getImages() != null) {
            for (Image item : entity.getImages()) {
                ImageResponseDTO itemDTO = new ImageResponseDTO();
                itemDTO.setId(item.getId());
                itemDTO.setFileName(item.getFileName());
                itemDTO.setFileData(item.getFileData() != null ? Base64.getEncoder().encodeToString(item.getFileData()) : null);

                itemDTOs.add(itemDTO);
            }
        }
        dto.setImages(itemDTOs);

        return dto;
    }

    @Override
    public void deleteFinalReport(Long id) throws EntityNotFoundException {
        FinalReport reportToDelete = finalReportDb.findByIdAndDeletedAtNull(id);
        if (reportToDelete == null) {
            throw new EntityNotFoundException("Klien tidak ditemukan");
        }

        reportToDelete.setDeletedAt(new Date());
        finalReportDb.save(reportToDelete);
    }

    @Override
    public byte[] getPdfFile(Long id) {
        FinalReport report = finalReportDb.findById(id);
        if (report == null) {
            throw new NoSuchElementException("Final Report not found!");
        }

        // Konversi FinalReport menjadi CreateFinalReportRequestDTO
        CreateFinalReportRequestDTO dto = new CreateFinalReportRequestDTO();
        dto.setEvent(report.getEvent());
        dto.setPerusahaan(report.getCompany());
        dto.setTanggal(report.getEventDate());

        List<Long> imageIds = new ArrayList<>();
        for (Image img : report.getImages()) {
            imageIds.add(img.getId());
        }
        dto.setImageListId(imageIds);

        // Panggil method generatePdfReport yang sudah ada
        CreateFinalReportResponseDTO responseDTO = generatePdfReport(dto, null);

        return responseDTO.getPdf();
    }


}
