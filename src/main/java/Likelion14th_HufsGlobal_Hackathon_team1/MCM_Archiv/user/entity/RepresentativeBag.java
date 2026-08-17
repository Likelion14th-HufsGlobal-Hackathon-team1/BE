package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "representative_bags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RepresentativeBag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;
}
