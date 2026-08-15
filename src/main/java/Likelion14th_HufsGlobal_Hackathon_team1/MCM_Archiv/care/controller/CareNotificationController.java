package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.controller;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto.CareNotificationListResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.service.CareNotificationService;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/care/notifications")
@RequiredArgsConstructor
public class CareNotificationController {

    private final CareNotificationService careNotificationService;

    @GetMapping
    public CareNotificationListResponse getNotifications(
            @LoginUser Long userId
    ) {
        return CareNotificationListResponse.from(
                careNotificationService.findAllByUserId(userId)
        );
    }

    @PatchMapping("/{notificationId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsRead(
            @LoginUser Long userId,
            @PathVariable Long notificationId
    ) {
        careNotificationService.markAsRead(notificationId, userId);
    }
}
