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
                IMPORTANT RULE: this image must contain zero text, letters, numbers, or
                writing of any kind — no captions, no labels, no signage, no city name
                written anywhere. The image must be 100%% typography-free.

                A full-bleed flat illustration filling the entire square canvas edge to
                edge — no border, no frame, no ring, no card or badge shape, no white
                margin around the artwork. The artwork itself must touch all four edges.
                Depict an artistic scene of landmarks and scenery from %s, %s, in a warm,
                colorful, elegant illustrative style, evoking this memory: "%s".
                No logos, no brand marks.
                """.formatted(city, country, memoLine);
    }
}
