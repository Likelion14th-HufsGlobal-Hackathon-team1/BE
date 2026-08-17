package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.entity.RepresentativeBag;

import java.util.List;

public record RepresentativeBagListResponse(List<RepresentativeBagResponse> bags) {

    public static RepresentativeBagListResponse from(List<RepresentativeBag> bags) {
        return new RepresentativeBagListResponse(
                bags.stream().map(RepresentativeBagResponse::from).toList()
        );
    }
}
