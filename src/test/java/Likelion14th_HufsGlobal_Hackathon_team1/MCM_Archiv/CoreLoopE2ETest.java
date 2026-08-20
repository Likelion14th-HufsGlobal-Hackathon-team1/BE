package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.security.JwtProvider;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.service.CloudflareImageClient;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.service.CloudinaryImageUploader;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.entity.User;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * 코어 루프 E2E (컨벤션 §11): 제품등록 → 여정인증 → 참생성 → 조회.
 * AI 이미지 생성(Cloudflare)·업로드(Cloudinary)는 외부 API라 MockitoBean으로 대체.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CoreLoopE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CloudflareImageClient cloudflareImageClient;

    @MockitoBean
    private CloudinaryImageUploader cloudinaryImageUploader;

    @Test
    void 제품등록_여정인증_참생성_조회_전체_흐름이_동작한다() throws Exception {
        when(cloudflareImageClient.generateImageBase64(anyString())).thenReturn("ZmFrZS1pbWFnZS1ieXRlcw==");
        when(cloudinaryImageUploader.upload(anyString())).thenReturn("https://cdn.example.com/charm-candidate.png");

        User user = userRepository.save(User.register(
                "홍길동", "e2e@mcm.com", "e2euser01",
                passwordEncoder.encode("password123"), "존나비"
        ));
        String token = jwtProvider.createAccessToken(user.getId());

        // 1) 제품등록
        String productResponse = mockMvc.perform(post("/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productCode":"E2E-MCM-001","productName":"Aren Shopper","purchaseDate":"2026-08-01"}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long productId = objectMapper.readTree(productResponse).get("productId").asLong();

        // 2) 여정인증 (AI 후보 3개 생성)
        String journeyResponse = mockMvc.perform(post("/journeys")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": %d, "country": "Japan", "city": "Tokyo",
                                  "memo": "여름 여행", "travelDate": "2026-08-01",
                                  "imageUrls": ["https://cdn.example.com/trip-photo.png"]
                                }
                                """.formatted(productId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.candidates.length()").value(3))
                .andReturn().getResponse().getContentAsString();
        String selectedImageUrl = objectMapper.readTree(journeyResponse)
                .get("candidates").get(0).get("imageUrl").asText();

        // 3) 참생성 (후보 중 1개 확정)
        String charmResponse = mockMvc.perform(post("/charms")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": %d, "country": "Japan", "city": "Tokyo",
                                  "memo": "여름 여행", "travelDate": "2026-08-01",
                                  "selectedImageUrl": "%s",
                                  "imageUrls": ["https://cdn.example.com/trip-photo.png"]
                                }
                                """.formatted(productId, selectedImageUrl)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long charmId = objectMapper.readTree(charmResponse).get("charmId").asLong();

        // 4) 조회
        mockMvc.perform(get("/charms/" + charmId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("Japan"))
                .andExpect(jsonPath("$.city").value("Tokyo"))
                .andExpect(jsonPath("$.aiImageUrl").value(selectedImageUrl))
                .andExpect(jsonPath("$.product.productId").value(productId));

        assertThat(charmId).isNotNull();
    }
}
