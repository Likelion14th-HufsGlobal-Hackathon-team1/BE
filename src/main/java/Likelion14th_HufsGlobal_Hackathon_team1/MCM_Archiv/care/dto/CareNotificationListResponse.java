package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.CareNotification;

import java.time.LocalDate;
import java.util.List;

public record CareNotificationListResponse(
        List<NotificationItem> notifications
) {

    public static CareNotificationListResponse from(
            List<CareNotification> notifications
    ) {
        return new CareNotificationListResponse(
                notifications.stream()
                        .map(NotificationItem::from)
                        .toList()
        );
    }

    public record NotificationItem(
            Long notificationId,
            Long productId,
            LocalDate targetDate,
            boolean isRead
    ) {

        public static NotificationItem from(CareNotification notification) {
            return new NotificationItem(
                    notification.getId(),
                    notification.getProductId(),
                    notification.getTargetDate(),
                    notification.isRead()
            );
        }
    }
}
