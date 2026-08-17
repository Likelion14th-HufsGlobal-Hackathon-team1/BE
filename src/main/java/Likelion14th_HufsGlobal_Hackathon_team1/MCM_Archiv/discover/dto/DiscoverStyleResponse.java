package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.entity.DiscoverStyle;

public record DiscoverStyleResponse(Long styleId, String imageUrl, String caption) {

    public static DiscoverStyleResponse from(DiscoverStyle style) {
        return new DiscoverStyleResponse(style.getId(), style.getImageUrl(), style.getCaption());
    }
}
