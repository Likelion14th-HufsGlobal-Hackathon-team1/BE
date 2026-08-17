package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.controller;

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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void 회원가입에_성공하면_201과_유저정보를_반환한다() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "홍길동", "nickname": "존나비",
                                  "email": "new@mcm.com", "loginId": "newuser01",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nickname").value("존나비"));
    }

    @Test
    void 이미_사용중인_아이디면_400을_반환한다() throws Exception {
        userRepository.save(User.register(
                "홍길동", "dup@mcm.com", "dupuser01",
                passwordEncoder.encode("password123"), "존나비"
        ));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "다른사람", "nickname": "다른닉",
                                  "email": "another@mcm.com", "loginId": "dupuser01",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_FAILED"));
    }

    @Test
    void 필수값이_비어있으면_400을_반환한다() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "", "nickname": "", "email": "", "loginId": "", "password": ""}
                                """))
                .andExpect(status().isBadRequest());
    }
}
