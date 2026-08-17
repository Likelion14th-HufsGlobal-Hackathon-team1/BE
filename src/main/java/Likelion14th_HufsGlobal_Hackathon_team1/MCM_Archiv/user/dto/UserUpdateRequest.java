package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.dto;

import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        String nickname,
        String profileImage,
        @Size(min = 8) String password
) {
}
