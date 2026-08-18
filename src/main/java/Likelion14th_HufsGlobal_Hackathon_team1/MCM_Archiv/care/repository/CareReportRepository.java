package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.repository;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.CareReport;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareReportRepository extends JpaRepository<CareReport, Long> {

    List<CareReport> findAllByProductIdInOrderByAnalyzedAtDesc(List<Long> productIds);
}