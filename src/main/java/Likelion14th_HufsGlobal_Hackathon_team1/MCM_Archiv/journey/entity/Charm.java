package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "charms",
        indexes = {
                @Index(name = "idx_charms_user_id", columnList = "user_id"),
                @Index(name = "idx_charms_product_id", columnList = "product_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Charm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charm_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 500)
    private String memo;

    @Column(name = "travel_date", nullable = false)
    private LocalDate travelDate;

    @Column(name = "ai_image_url", nullable = false, length = 1000)
    private String aiImageUrl;

    @OneToMany(mappedBy = "charm", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CharmImage> images = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Builder
    private Charm(Long userId, Long productId, String country, String city,
                  String memo, LocalDate travelDate, String aiImageUrl) {
        this.userId = userId;
        this.productId = productId;
        this.country = country;
        this.city = city;
        this.memo = memo;
        this.travelDate = travelDate;
        this.aiImageUrl = aiImageUrl;
    }

    public void addImage(CharmImage image) {
        images.add(image);
        image.assignCharm(this);
    }
}
