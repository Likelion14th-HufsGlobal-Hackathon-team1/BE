package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.entity.User;

public record UserRegisterResponse(Long userId, String nickname) {

    public static UserRegisterResponse from(User user) {
        return new UserRegisterResponse(user.getId(), user.getNickname());
    }
}
