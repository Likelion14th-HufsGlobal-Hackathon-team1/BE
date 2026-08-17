package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CareReservationRequest(

        @NotNull
        Long careId,

        @NotNull
        Long storeId,

        @NotNull
        LocalDate reservationDate,

        @NotNull
        LocalTime reservationTime

) {
}
