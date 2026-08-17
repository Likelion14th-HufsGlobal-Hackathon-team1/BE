package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(
        @NotBlank String loginId,
        @NotBlank String password
) {
}
