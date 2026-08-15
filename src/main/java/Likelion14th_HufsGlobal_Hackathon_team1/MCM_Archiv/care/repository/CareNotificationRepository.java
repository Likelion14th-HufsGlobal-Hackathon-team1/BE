package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.repository;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.CareNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareNotificationRepository
        extends JpaRepository<CareNotification, Long> {

    List<CareNotification> findAllByProductIdIn(List<Long> productIds);
}
