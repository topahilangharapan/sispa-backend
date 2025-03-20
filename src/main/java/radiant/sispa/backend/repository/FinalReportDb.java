package radiant.sispa.backend.repository;

import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import radiant.sispa.backend.model.FinalReport;
import radiant.sispa.backend.model.Image;
import java.time.Instant;


@Transactional
public interface FinalReportDb extends JpaRepository<FinalReport, String> {
    FinalReport findById(Long id);
    FinalReport findByIdAndDeletedAtNull(Long id);
    List<FinalReport> findByDeletedAtNull();
}
