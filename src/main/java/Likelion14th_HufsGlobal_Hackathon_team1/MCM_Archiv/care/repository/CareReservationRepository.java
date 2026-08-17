package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.repository;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.CareReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareReservationRepository
        extends JpaRepository<CareReservation, Long> {

    boolean existsByCareId(Long careId);
}
