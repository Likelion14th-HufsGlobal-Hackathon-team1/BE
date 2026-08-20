package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.repository;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.entity.Product;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void 본인이_등록한_productCode로_조회하면_찾는다() {
        Product product = Product.register(1L, "MCM-SCOPE-001", "Aren Shopper", null, null, null, LocalDate.now());
        productRepository.save(product);

        Optional<Product> found = productRepository.findByProductCodeAndUserId("MCM-SCOPE-001", 1L);

        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(1L);
    }

    @Test
    void 다른_유저가_등록한_productCode로_조회하면_비어있다() {
        Product product = Product.register(1L, "MCM-SCOPE-002", "Aren Shopper", null, null, null, LocalDate.now());
        productRepository.save(product);

        Optional<Product> found = productRepository.findByProductCodeAndUserId("MCM-SCOPE-002", 2L);

        assertThat(found).isEmpty();
    }

    @Test
    void 존재하지_않는_productCode로_조회하면_비어있다() {
        Optional<Product> found = productRepository.findByProductCodeAndUserId("NOT-EXIST", 1L);

        assertThat(found).isEmpty();
    }
}
