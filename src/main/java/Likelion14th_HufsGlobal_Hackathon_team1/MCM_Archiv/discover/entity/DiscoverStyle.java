package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "discover_styles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiscoverStyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String caption;

    private DiscoverStyle(String imageUrl, String caption) {
        this.imageUrl = imageUrl;
        this.caption = caption;
    }

    public static DiscoverStyle of(String imageUrl, String caption) {
        return new DiscoverStyle(imageUrl, caption);
    }
}
