package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.CareReport;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.entity.Product;
import java.time.Instant;
import java.util.List;

public record CareReportListResponse(List<CareReportListItem> reports) {

    public record CareReportListItem(
            Long careId,
            Integer totalScore,
            Instant analyzedAt,
            ProductSummary product
    ) {

        public record ProductSummary(Long productId, String productName) {
        }

        public static CareReportListItem from(CareReport report, Product product) {
            return new CareReportListItem(
                    report.getId(),
                    report.getTotalScore(),
                    report.getAnalyzedAt(),
                    new ProductSummary(product.getId(), product.getProductName())
            );
        }
    }
}
