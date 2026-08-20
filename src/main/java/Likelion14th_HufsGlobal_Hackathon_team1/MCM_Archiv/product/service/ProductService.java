package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.BusinessException;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.ErrorCode;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.entity.Product;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Product register(
            Long userId,
            String productCode,
            String productName,
            String productImage,
            LocalDate purchaseDate
    ) {
        Product product = Product.register(
                userId,
                productCode,
                productName,
                productImage,
                purchaseDate
        );

        Product saved = productRepository.save(product);
        eventPublisher.publishEvent(new ProductRegisteredEvent(saved.getId(), saved.getPurchaseDate()));

        return saved;
    }

    public List<Product> findAllByUserId(Long userId) {
        return productRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    public Product findByIdAndUserId(Long productId, Long userId) {
        return productRepository.findByIdAndUserId(productId, userId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.NOT_FOUND,
                                "제품을 찾을 수 없습니다."
                        )
                );
    }

    public Product findByProductCodeAndUserId(String productCode, Long userId) {
        return productRepository.findByProductCodeAndUserId(productCode, userId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.NOT_FOUND,
                                "제품을 찾을 수 없습니다."
                        )
                );
    }
}
