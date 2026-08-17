package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.entity.User;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void 아이디와_비밀번호가_맞으면_토큰을_발급한다() throws Exception {
        userRepository.save(User.register(
                "홍길동", "hong@mcm.com", "hong0811",
                passwordEncoder.encode("password123"), "존나비"
        ));

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"loginId":"hong0811","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.nickname").value("존나비"));
    }

    @Test
    void 비밀번호가_틀리면_401을_반환한다() throws Exception {
        userRepository.save(User.register(
                "홍길동", "hong2@mcm.com", "hong0812",
                passwordEncoder.encode("password123"), "존나비"
        ));

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"loginId":"hong0812","password":"wrong-password"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    }

    @Test
    void 존재하지_않는_아이디면_401을_반환한다() throws Exception {
        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"loginId":"no-such-id","password":"password123"}
                                """))
                .andExpect(status().isUnauthorized());
    }
}
