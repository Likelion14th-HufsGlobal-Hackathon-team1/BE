package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto;

import java.util.List;
import java.util.stream.IntStream;

public record JourneyVerifyResponse(List<CandidateResponse> candidates) {

    public record CandidateResponse(Integer candidateId, String imageUrl) {
    }

    public static JourneyVerifyResponse from(List<String> imageUrls) {
        List<CandidateResponse> candidates = IntStream.range(0, imageUrls.size())
                .mapToObj(i -> new CandidateResponse(i + 1, imageUrls.get(i)))
                .toList();
        return new JourneyVerifyResponse(candidates);
    }
}