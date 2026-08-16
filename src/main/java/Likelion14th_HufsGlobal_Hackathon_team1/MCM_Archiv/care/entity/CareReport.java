package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
        name = "care_reports",
        indexes = {
                @Index(name = "idx_care_reports_product_id", columnList = "product_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "care_id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;

    @Column(name = "scratch_score", nullable = false)
    private Integer scratchScore;

    @Column(name = "stain_score", nullable = false)
    private Integer stainScore;

    @Column(name = "wear_score", nullable = false)
    private Integer wearScore;

    @Column(name = "ai_comment", length = 500)
    private String aiComment;

    @Column(name = "analyzed_at", nullable = false)
    private Instant analyzedAt;

    private CareReport(Long productId, Integer totalScore, Integer scratchScore,
                       Integer stainScore, Integer wearScore, String aiComment) {
        this.productId = productId;
        this.totalScore = totalScore;
        this.scratchScore = scratchScore;
        this.stainScore = stainScore;
        this.wearScore = wearScore;
        this.aiComment = aiComment;
        this.analyzedAt = Instant.now();
    }

    public static CareReport analyze(Long productId, Integer totalScore, Integer scratchScore,
                                     Integer stainScore, Integer wearScore, String aiComment) {
        return new CareReport(productId, totalScore, scratchScore, stainScore, wearScore, aiComment);
    }
}