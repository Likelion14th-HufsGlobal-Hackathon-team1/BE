package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.controller;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.security.LoginUser;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.dto.ProductDetailResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.dto.ProductListResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.dto.ProductRegisterRequest;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.dto.ProductRegisterResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.entity.Product;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductRegisterResponse create(
            @LoginUser Long userId,
            @Valid @RequestBody ProductRegisterRequest request
    ) {
        Product product = productService.register(
                userId,
                request.productCode(),
                request.productName(),
                request.productImage(),
                request.nickname(),
                request.memoryCapsule(),
                request.purchaseDate()
        );

        return ProductRegisterResponse.from(product);
    }

    @GetMapping
    public ProductListResponse findAll(
            @LoginUser Long userId
    ) {
        return ProductListResponse.from(
                productService.findAllByUserId(userId)
        );
    }

    @GetMapping("/{productId}")
    public ProductDetailResponse findById(
            @LoginUser Long userId,
            @PathVariable Long productId
    ) {
        Product product =
                productService.findByIdAndUserId(productId, userId);

        return ProductDetailResponse.from(product);
    }

    @GetMapping("/scan")
    public ProductDetailResponse scan(
            @LoginUser Long userId,
            @RequestParam String code
    ) {
        Product product =
                productService.findByProductCodeAndUserId(code, userId);

        return ProductDetailResponse.from(product);
    }
}
