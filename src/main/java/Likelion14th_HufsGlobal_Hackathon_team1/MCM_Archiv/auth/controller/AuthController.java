package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.auth.controller;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.auth.dto.AuthLoginRequest;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.auth.dto.AuthLoginResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth")
    public AuthLoginResponse login(@Valid @RequestBody AuthLoginRequest request) {
        return authService.login(request.loginId(), request.password());
    }
}
