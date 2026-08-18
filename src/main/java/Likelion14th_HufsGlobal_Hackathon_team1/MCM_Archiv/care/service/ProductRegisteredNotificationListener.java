package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.CareNotification;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.repository.CareNotificationRepository;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.service.ProductRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 제품 등록(purchase_date 기준) 6개월 뒤 케어 알림을 예약한다 (컨벤션 §6-2).
 * product/ 도메인이 care/를 직접 참조하지 않도록 이벤트로 느슨하게 연결.
 */
@Component
@RequiredArgsConstructor
public class ProductRegisteredNotificationListener {

    private static final int CARE_REMINDER_MONTHS = 6;

    private final CareNotificationRepository careNotificationRepository;

    @EventListener
    public void onProductRegistered(ProductRegisteredEvent event) {
        if (event.purchaseDate() == null) {
            return;
        }

        careNotificationRepository.save(
                CareNotification.of(event.productId(), event.purchaseDate().plusMonths(CARE_REMINDER_MONTHS))
        );
    }
}
