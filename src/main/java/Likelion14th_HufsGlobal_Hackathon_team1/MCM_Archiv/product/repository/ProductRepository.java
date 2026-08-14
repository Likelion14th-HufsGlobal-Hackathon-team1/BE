package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.repository;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndUserId(Long id, Long userId);

    List<Product> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Product> findByProductCode(String productCode);

    boolean existsByProductCode(String productCode);
}
