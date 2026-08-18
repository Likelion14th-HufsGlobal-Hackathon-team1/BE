package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "stores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String phone;

    private Store(
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            String phone
    ) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
    }

    public static Store of(
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            String phone
    ) {
        return new Store(
                name,
                address,
                latitude,
                longitude,
                phone
        );
    }
}
