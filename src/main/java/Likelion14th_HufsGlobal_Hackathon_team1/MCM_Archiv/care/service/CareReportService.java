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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareReportService {

    private final CareReportRepository careReportRepository;
    private final ProductService productService;
    private final OpenAiVisionClient openAiVisionClient;

    @Transactional
    public CareReportResponse createReport(Long userId, CareReportCreateRequest request) {
        productService.findByIdAndUserId(request.productId(), userId);

        OpenAiVisionClient.CareAnalysisResult result = openAiVisionClient.analyzeBagImage(request.imageUrl());
        int total = (result.scratchScore() + result.stainScore() + result.wearScore()) / 3;

        CareReport report = CareReport.analyze(
                request.productId(), request.imageUrl(), total,
                result.scratchScore(), result.stainScore(), result.wearScore(), result.comment()
        );

        return CareReportResponse.from(careReportRepository.save(report));
    }

    public CareReportResponse findReport(Long userId, Long careId) {
        CareReport report = careReportRepository.findById(careId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "진단 결과를 찾을 수 없습니다."));

        productService.findByIdAndUserId(report.getProductId(), userId);

        return CareReportResponse.from(report);
    }
}