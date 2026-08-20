package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.BusinessException;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.ErrorCode;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto.CharmCreateRequest;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto.CharmCreateResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto.CharmDetailResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto.CharmListResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto.CharmPositionUpdateRequest;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.entity.Charm;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.entity.CharmImage;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.repository.CharmRepository;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * POST /charms — 후보 중 선택된 1개 확정 저장. charm + charm_images 한 트랜잭션 (컨벤션 §10).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CharmService {

    private final CharmRepository charmRepository;
    private final ProductService productService;

    @Transactional
    public CharmCreateResponse createCharm(Long userId, CharmCreateRequest request) {
        productService.findByIdAndUserId(request.productId(), userId);

        Charm charm = Charm.builder()
                .userId(userId)
                .productId(request.productId())
                .country(request.country())
                .city(request.city())
                .memo(request.memo())
                .travelDate(request.travelDate())
                .aiImageUrl(request.selectedImageUrl())
                .build();

        if (request.imageUrls() != null) {
            request.imageUrls().forEach(url ->
                    charm.addImage(CharmImage.builder().imageUrl(url).build())
            );
        }

        Charm saved = charmRepository.save(charm);
        return CharmCreateResponse.from(saved);
    }

    public CharmListResponse findCharms(Long userId) {
        List<Charm> charms = charmRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        return CharmListResponse.from(charms);
    }

    public CharmDetailResponse findCharm(Long userId, Long charmId) {
        Charm charm = charmRepository.findByIdAndUserId(charmId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "참을 찾을 수 없습니다."));

        var product = productService.findByIdAndUserId(charm.getProductId(), userId);

        return CharmDetailResponse.from(charm, product);
    }

    @Transactional
    public CharmDetailResponse updatePosition(Long userId, Long charmId, CharmPositionUpdateRequest request) {
        Charm charm = charmRepository.findByIdAndUserId(charmId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "참을 찾을 수 없습니다."));

        charm.updatePosition(request.positionX(), request.positionY(), request.rotation(), request.scale());

        var product = productService.findByIdAndUserId(charm.getProductId(), userId);
        return CharmDetailResponse.from(charm, product);
    }
}