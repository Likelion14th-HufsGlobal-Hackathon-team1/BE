package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto.JourneyVerifyRequest;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto.JourneyVerifyResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * POST /journeys — AI 참 후보 3개 생성. DB 저장 없음 (API계약서 §3).
 */
@Service
@RequiredArgsConstructor
public class JourneyService {

    private final ProductService productService;
    private final CharmCandidateImageService charmCandidateImageService;

    public JourneyVerifyResponse verifyJourney(Long userId, JourneyVerifyRequest request) {
        productService.findByIdAndUserId(request.productId(), userId);

        List<String> candidateImageUrls = charmCandidateImageService.generateCandidateImageUrls(
                request.country(), request.city(), request.memo(), request.imageUrls()
        );

        return JourneyVerifyResponse.from(candidateImageUrls);
    }
}