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
                A luxury bag charm keyring pendant representing a trip to %s, %s.
                Design a distinctive charm motif inspired by a landmark or cultural symbol
                of the destination, styled to reflect this travel memory: "%s".
                Elegant enamel-style charm with a refined gold-trimmed border and a small
                metal attachment ring at the top, isolated on a soft neutral background,
                studio product photography lighting, square composition.
                Absolutely no text, no letters, no logos, no brand marks of any kind.
                """.formatted(city, country, memoLine);
    }
}
