package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.CareReservation;

import java.time.LocalDate;
import java.time.LocalTime;

public record CareReservationResponse(
        Long reservationId,
        Long storeId,
        LocalDate reservationDate,
        LocalTime reservationTime,
        String status
) {

    public static CareReservationResponse from(CareReservation reservation) {
        return new CareReservationResponse(
                reservation.getId(),
                reservation.getStoreId(),
                reservation.getReservationDate(),
                reservation.getReservationTime(),
                reservation.getStatus()
        );
    }
}
