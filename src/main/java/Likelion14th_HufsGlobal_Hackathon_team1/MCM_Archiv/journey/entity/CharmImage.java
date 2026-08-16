package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "charm_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CharmImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charm_id", nullable = false)
    private Charm charm;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Builder
    private CharmImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    void assignCharm(Charm charm) {
        this.charm = charm;
    }
}
