package radiant.sispa.backend.restservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
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
import radiant.sispa.backend.restdto.response.CreateFinalReportResponseDTO;
import radiant.sispa.backend.restdto.response.CreateInvoiceResponseDTO;
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
    public CreateFinalReportResponseDTO generatePdfReport(String rawData, List<MultipartFile> images, String authHeader) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode = objectMapper.readTree(rawData);
            CreateFinalReportRequestDTO createFinalReportRequestDTO = objectMapper.treeToValue(jsonNode, CreateFinalReportRequestDTO.class);

            String token = authHeader.substring(7);
            String createdBy = jwtUtils.getUserNameFromJwtToken(token);

            FinalReport finalReport = createFinalReportRequestToFinalReport(createFinalReportRequestDTO, images, createdBy);

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

    private FinalReport createFinalReportRequestToFinalReport(CreateFinalReportRequestDTO createFinalReportRequestDTO, List<MultipartFile> images, String createdBy) throws IOException {
        FinalReport finalReport = new FinalReport();

        finalReport.setCreatedBy(createdBy);
        finalReport.setImages(saveImages(images, createdBy));
        finalReport.setEvent(createFinalReportRequestDTO.getEvent());
        finalReport.setCompany(createFinalReportRequestDTO.getPerusahaan());
        finalReport.setEventDate(createFinalReportRequestDTO.getTanggal());
        finalReport.setFileName(createFileName(finalReport));

        return finalReportDb.save(finalReport);
    }


    private String createFileName(FinalReport finalReport) {
        return String.format("Final Report %s", finalReport.getEvent());
    }

}
