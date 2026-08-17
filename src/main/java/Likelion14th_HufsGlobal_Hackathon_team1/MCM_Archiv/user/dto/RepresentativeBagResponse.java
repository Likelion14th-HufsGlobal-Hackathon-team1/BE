package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.entity.RepresentativeBag;

public record RepresentativeBagResponse(Long bagId, String name, String imageUrl, boolean isDefault) {

    public static RepresentativeBagResponse from(RepresentativeBag bag) {
        return new RepresentativeBagResponse(bag.getId(), bag.getName(), bag.getImageUrl(), bag.isDefault());
    }
}
