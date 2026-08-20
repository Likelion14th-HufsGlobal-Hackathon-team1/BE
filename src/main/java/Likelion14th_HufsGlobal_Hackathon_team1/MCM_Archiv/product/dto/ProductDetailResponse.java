package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.entity.Product;

import java.time.Instant;
import java.time.LocalDate;

public record ProductDetailResponse(
        Long productId,
        String productName,
        String productCode,
        String productImage,
        String nickname,
        String memoryCapsule,
        LocalDate purchaseDate,
        Instant registeredAt,
        boolean isVerified
) {

    public static ProductDetailResponse from(Product product) {
        return new ProductDetailResponse(
                product.getId(),
                product.getProductName(),
                product.getProductCode(),
                product.getProductImage(),
                product.getNickname(),
                product.getMemoryCapsule(),
                product.getPurchaseDate(),
                product.getRegisteredAt(),
                product.isVerified()
        );
    }
}
