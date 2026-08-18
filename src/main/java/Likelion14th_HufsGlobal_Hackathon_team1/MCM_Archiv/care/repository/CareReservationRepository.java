package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.repository;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.CareReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CareReservationRepository
        extends JpaRepository<CareReservation, Long> {

    boolean existsByCareId(Long careId);

    List<CareReservation> findAllByStoreIdAndReservationDate(
            Long storeId,
            LocalDate reservationDate
    );

    boolean existsByStoreIdAndReservationDateAndReservationTime(
            Long storeId,
            LocalDate reservationDate,
            LocalTime reservationTime
    );
}
