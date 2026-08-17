package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "care_reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "care_id", nullable = false, unique = true)
    private Long careId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "reservation_time", nullable = false)
    private LocalTime reservationTime;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    private CareReservation(
            Long careId,
            Long storeId,
            LocalDate reservationDate,
            LocalTime reservationTime
    ) {
        this.careId = careId;
        this.storeId = storeId;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.status = "PENDING";
        this.createdAt = Instant.now();
    }

    public static CareReservation create(
            Long careId,
            Long storeId,
            LocalDate reservationDate,
            LocalTime reservationTime
    ) {
        return new CareReservation(
                careId,
                storeId,
                reservationDate,
                reservationTime
        );
    }
}
