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
                A single luxury enamel charm pendant representing a trip to %s, %s, in a
                distinctive pendant silhouette shape (not a plain rectangle), with a
                refined gold-trimmed border. Design a motif inspired by a landmark or
                cultural symbol of the destination, styled to reflect this travel memory:
                "%s".
                Flat product illustration on a solid pure white (#FFFFFF) background,
                no shadow, no gradient, no vignette, hard clean edges, square composition.
                Absolutely no text, no letters, no logos, no brand marks, no keyring ring,
                no chain, no hardware — only the charm pendant face itself.
                """.formatted(city, country, memoLine);
    }
}
