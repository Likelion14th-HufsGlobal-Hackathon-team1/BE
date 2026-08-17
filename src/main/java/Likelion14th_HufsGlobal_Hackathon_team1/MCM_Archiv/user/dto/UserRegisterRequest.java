package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
        @NotBlank String name,
        @NotBlank String nickname,
        @NotBlank @Email String email,
        @NotBlank String loginId,
        @NotBlank @Size(min = 8) String password
) {
}
