package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.entity;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "representative_bag_id")
    private RepresentativeBag representativeBag;

    private User(String name, String email, String loginId, String encodedPassword, String nickname) {
        this.name = name;
        this.email = email;
        this.loginId = loginId;
        this.password = encodedPassword;
        this.nickname = nickname;
    }

    public static User register(String name, String email, String loginId, String encodedPassword, String nickname) {
        return new User(name, email, loginId, encodedPassword, nickname);
    }

    public void updateProfile(String nickname, String profileImage) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (profileImage != null) {
            this.profileImage = profileImage;
        }
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
