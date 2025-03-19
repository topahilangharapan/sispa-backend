package radiant.sispa.backend.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import radiant.sispa.backend.model.FinalReport;
import radiant.sispa.backend.model.Image;

@Transactional
public interface FinalReportDb extends JpaRepository<FinalReport, String> {

}
