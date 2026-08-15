package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "care_notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public void markAsRead() {
        this.isRead = true;
    }
}
