package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.entity.Product;

import java.util.List;

public record ProductListResponse(
        List<ProductItem> products
) {

    public static ProductListResponse from(List<Product> products) {
        return new ProductListResponse(
                products.stream()
                        .map(ProductItem::from)
                        .toList()
        );
    }

    public record ProductItem(
            Long productId,
            String productName,
            String productImage
    ) {

        public static ProductItem from(Product product) {
            return new ProductItem(
                    product.getId(),
                    product.getProductName(),
                    product.getProductImage()
            );
        }
    }
}
