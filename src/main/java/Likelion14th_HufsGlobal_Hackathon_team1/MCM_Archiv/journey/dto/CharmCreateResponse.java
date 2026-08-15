package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.entity.Charm;

import java.time.Instant;

public record CharmCreateResponse(Long charmId, String aiImageUrl, Instant createdAt) {

    public static CharmCreateResponse from(Charm charm) {
        return new CharmCreateResponse(charm.getId(), charm.getAiImageUrl(), charm.getCreatedAt());
    }
}