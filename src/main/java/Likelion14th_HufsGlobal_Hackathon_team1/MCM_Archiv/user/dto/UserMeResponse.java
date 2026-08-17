package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.entity.User;

public record UserMeResponse(
        Long userId,
        String name,
        String nickname,
        String email,
        String loginId,
        String profileImage,
        Long representativeBagId
) {

    public static UserMeResponse from(User user) {
        return new UserMeResponse(
                user.getId(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getLoginId(),
                user.getProfileImage(),
                user.getRepresentativeBag() != null ? user.getRepresentativeBag().getId() : null
        );
    }
}
