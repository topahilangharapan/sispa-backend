package radiant.sispa.backend.restdto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import radiant.sispa.backend.model.Image;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateFinalReportRequestDTO {
    private String event;
    private String tanggal;
    private String perusahaan;
    private List<Long> imageListId;
}
