package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.entity.DiscoverStyle;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.repository.DiscoverStyleRepository;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.security.JwtProvider;
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
class DiscoverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DiscoverStyleRepository discoverStyleRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void 인증된_사용자는_스타일_목록을_조회한다() throws Exception {
        discoverStyleRepository.save(DiscoverStyle.of("https://cdn.example.com/style.png", "미니멀 오피스룩"));
        String token = jwtProvider.createAccessToken(1L);

        mockMvc.perform(get("/discover")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.styles[0].caption").value("미니멀 오피스룩"));
    }

    @Test
    void 토큰이_없으면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/discover"))
                .andExpect(status().isUnauthorized());
    }
}
