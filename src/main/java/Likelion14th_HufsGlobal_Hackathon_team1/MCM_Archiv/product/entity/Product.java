package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_products_user_id", columnList = "user_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_code", nullable = false, unique = true)
    private String productCode;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "model_number")
    private String modelNumber;

    @Column(name = "product_image")
    private String productImage;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "registered_at")
    private Instant registeredAt;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    private Product(
            Long userId,
            String productCode,
            String productName,
            String productImage,
            LocalDate purchaseDate
    ) {
        Instant now = Instant.now();

        this.userId = userId;
        this.productCode = productCode;
        this.productName = productName;
        this.productImage = productImage;
        this.purchaseDate = purchaseDate;
        this.registeredAt = now;
        this.createdAt = now;
        this.isVerified = true;
    }

    public static Product register(
            Long userId,
            String productCode,
            String productName,
            String productImage,
            LocalDate purchaseDate
    ) {
        return new Product(
                userId,
                productCode,
                productName,
                productImage,
                purchaseDate
        );
    }
}
