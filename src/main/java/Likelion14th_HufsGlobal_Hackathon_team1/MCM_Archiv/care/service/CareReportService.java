package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto.CareReportCreateRequest;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto.CareReportResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.CareReport;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.repository.CareReportRepository;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.BusinessException;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.ErrorCode;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareReportService {

    private final CareReportRepository careReportRepository;
    private final ProductService productService;

    @Transactional
    public CareReportResponse createReport(Long userId, CareReportCreateRequest request) {
        productService.findByIdAndUserId(request.productId(), userId);

        int scratch = randomScore();
        int stain = randomScore();
        int wear = randomScore();
        int total = (scratch + stain + wear) / 3;

        CareReport report = CareReport.analyze(
                request.productId(), total, scratch, stain, wear, buildComment(total)
        );

        return CareReportResponse.from(careReportRepository.save(report));
    }

    public CareReportResponse findReport(Long userId, Long careId) {
        CareReport report = careReportRepository.findById(careId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "진단 결과를 찾을 수 없습니다."));

        productService.findByIdAndUserId(report.getProductId(), userId);

        return CareReportResponse.from(report);
    }

    private int randomScore() {
        return ThreadLocalRandom.current().nextInt(70, 101);
    }

    private String buildComment(int total) {
        if (total >= 90) return "전반적으로 관리가 잘 된 상태입니다.";
        if (total >= 80) return "가벼운 마모가 보이지만 양호한 상태입니다.";
        return "케어가 필요한 상태입니다. 매장 방문을 권장드려요.";
    }
}