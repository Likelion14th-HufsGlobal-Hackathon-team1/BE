package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.security.JwtProvider;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.entity.Product;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.repository.ProductRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductScanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void 본인이_등록한_제품코드로_스캔하면_200을_반환한다() throws Exception {
        productRepository.save(Product.register(1L, "SCAN-OWN-001", "Aren Shopper", LocalDate.now()));
        String token = jwtProvider.createAccessToken(1L);

        mockMvc.perform(get("/products/scan")
                        .param("code", "SCAN-OWN-001")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productCode").value("SCAN-OWN-001"));
    }

    @Test
    void 남이_등록한_제품코드로_스캔하면_404를_반환한다() throws Exception {
        productRepository.save(Product.register(1L, "SCAN-OTHER-001", "Aren Shopper", LocalDate.now()));
        String token = jwtProvider.createAccessToken(2L);

        mockMvc.perform(get("/products/scan")
                        .param("code", "SCAN-OTHER-001")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }

    @Test
    void 미등록_코드로_스캔하면_404를_반환한다() throws Exception {
        String token = jwtProvider.createAccessToken(1L);

        mockMvc.perform(get("/products/scan")
                        .param("code", "NEVER-REGISTERED")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void 토큰이_없으면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/products/scan").param("code", "ANY-CODE"))
                .andExpect(status().isUnauthorized());
    }
}
