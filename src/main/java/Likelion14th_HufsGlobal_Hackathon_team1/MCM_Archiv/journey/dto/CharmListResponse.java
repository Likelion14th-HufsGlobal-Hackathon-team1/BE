package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.entity.Charm;

import java.time.LocalDate;
import java.util.List;

public record CharmListResponse(int totalCountries, int totalJourneys, List<CharmSummaryResponse> charms) {

    public record CharmSummaryResponse(
            Long charmId, String aiImageUrl, String country, String city, LocalDate travelDate,
            Double positionX, Double positionY, Double rotation, Double scale
    ) {
        public static CharmSummaryResponse from(Charm charm) {
            return new CharmSummaryResponse(
                    charm.getId(), charm.getAiImageUrl(), charm.getCountry(), charm.getCity(), charm.getTravelDate(),
                    charm.getPositionX(), charm.getPositionY(), charm.getRotation(), charm.getScale()
            );
        }
    }

    public static CharmListResponse from(List<Charm> charms) {
        List<CharmSummaryResponse> summaries = charms.stream().map(CharmSummaryResponse::from).toList();
        int totalCountries = (int) charms.stream().map(Charm::getCountry).distinct().count();
        return new CharmListResponse(totalCountries, charms.size(), summaries);
    }
}