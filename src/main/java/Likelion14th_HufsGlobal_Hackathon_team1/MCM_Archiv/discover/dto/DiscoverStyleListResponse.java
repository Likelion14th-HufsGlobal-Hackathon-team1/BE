package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.entity.DiscoverStyle;

import java.util.List;

public record DiscoverStyleListResponse(List<DiscoverStyleResponse> styles) {

    public static DiscoverStyleListResponse from(List<DiscoverStyle> styles) {
        return new DiscoverStyleListResponse(
                styles.stream().map(DiscoverStyleResponse::from).toList()
        );
    }
}
