package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.entity.Charm;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.entity.CharmImage;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.entity.Product;

import java.time.LocalDate;
import java.util.List;

public record CharmDetailResponse(
        Long charmId,
        String aiImageUrl,
        String country,
        String city,
        String memo,
        LocalDate travelDate,
        ProductSummary product,
        List<String> images
) {

    public record ProductSummary(Long productId, String productName) {
    }

    public static CharmDetailResponse from(Charm charm, Product product) {
        return new CharmDetailResponse(
                charm.getId(),
                charm.getAiImageUrl(),
                charm.getCountry(),
                charm.getCity(),
                charm.getMemo(),
                charm.getTravelDate(),
                new ProductSummary(product.getId(), product.getProductName()),
                charm.getImages().stream().map(CharmImage::getImageUrl).toList()
        );
    }
}