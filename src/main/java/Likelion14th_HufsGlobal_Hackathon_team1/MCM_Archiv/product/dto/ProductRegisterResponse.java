package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.entity.Product;

import java.time.Instant;

public record ProductRegisterResponse(
        Long productId,
        String productCode,
        String productName,
        String productImage,
        boolean isVerified,
        Instant registeredAt
) {

    public static ProductRegisterResponse from(Product product) {
        return new ProductRegisterResponse(
                product.getId(),
                product.getProductCode(),
                product.getProductName(),
                product.getProductImage(),
                product.isVerified(),
                product.getRegisteredAt()
        );
    }
}
