package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.CareNotification;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.repository.CareNotificationRepository;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.BusinessException;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.ErrorCode;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.entity.Product;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareNotificationService {

    private final CareNotificationRepository careNotificationRepository;
    private final ProductService productService;

    public List<CareNotification> findAllByUserId(Long userId) {
        List<Long> productIds = productService.findAllByUserId(userId)
                .stream()
                .map(Product::getId)
                .toList();

        if (productIds.isEmpty()) {
            return List.of();
        }

        return careNotificationRepository.findAllByProductIdIn(productIds);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        CareNotification notification = careNotificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.NOT_FOUND,
                                "알림을 찾을 수 없습니다."
                        )
                );

        productService.findByIdAndUserId(
                notification.getProductId(),
                userId
        );

        notification.markAsRead();
    }
}
