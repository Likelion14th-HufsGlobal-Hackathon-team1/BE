package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.controller;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.security.LoginUser;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.dto.RepresentativeBagListResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.dto.UserMeResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.dto.UserRegisterRequest;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.dto.UserRegisterResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.dto.UserUpdateRequest;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.entity.User;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegisterResponse create(@Valid @RequestBody UserRegisterRequest request) {
        User user = userService.register(
                request.name(),
                request.nickname(),
                request.email(),
                request.loginId(),
                request.password()
        );

        return UserRegisterResponse.from(user);
    }

    @GetMapping("/users/me")
    public UserMeResponse findMe(@LoginUser Long userId) {
        return UserMeResponse.from(userService.findById(userId));
    }

    @PatchMapping("/users/me")
    public UserMeResponse updateMe(
            @LoginUser Long userId,
            @RequestBody UserUpdateRequest request
    ) {
        return UserMeResponse.from(userService.update(userId, request));
    }

    @DeleteMapping("/users/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMe(@LoginUser Long userId) {
        userService.withdraw(userId);
    }

    @GetMapping("/representative-bags")
    public RepresentativeBagListResponse findRepresentativeBags() {
        return RepresentativeBagListResponse.from(userService.findRepresentativeBags());
    }
}
