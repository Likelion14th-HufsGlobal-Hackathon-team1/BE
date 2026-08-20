package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CharmCandidateImageService {

    private static final int CANDIDATE_COUNT = 3;

    private final CloudflareImageClient cloudflareImageClient;
    private final CloudinaryImageUploader imageUploader;

    public List<String> generateCandidateImageUrls(String country, String city, String memo) {
        String prompt = buildPrompt(country, city, memo);

        return IntStream.range(0, CANDIDATE_COUNT)
                .mapToObj(i -> cloudflareImageClient.generateImageBase64(prompt))
                .map(imageUploader::upload)
                .toList();
    }

    private String buildPrompt(String country, String city, String memo) {
        String memoLine = (memo == null || memo.isBlank()) ? "a memorable trip" : memo;

        return """
                A single square decorative illustration representing a trip to %s, %s.
                Design an abstract artistic motif inspired by a landmark or cultural symbol
                of the destination, styled to reflect this travel memory: "%s".
                Warm, colorful, elegant illustrative style, centered composition, suitable
                to be displayed inside a circular photo frame.
                The design contains absolutely NO text, NO letters, NO words, and the city
                or country name is NEVER written anywhere on it — motif only, no typography
                at all. No logos, no brand marks.
                Flat product illustration on a solid pure white (#FFFFFF) background,
                no shadow, no gradient, no vignette, square composition.
                """.formatted(city, country, memoLine);
    }
}
