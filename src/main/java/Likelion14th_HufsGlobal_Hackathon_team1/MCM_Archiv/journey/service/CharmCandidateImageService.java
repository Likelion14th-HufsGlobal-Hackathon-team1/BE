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

    public List<String> generateCandidateImageUrls(String country, String city, String productName) {
        String prompt = buildPrompt(country, city, productName);

        return IntStream.range(0, CANDIDATE_COUNT)
                .mapToObj(i -> cloudflareImageClient.generateImageBase64(prompt))
                .map(imageUploader::upload)
                .toList();
    }

    private String buildPrompt(String country, String city, String productName) {
        return """
                A charm-style illustration combining a landmark of %s, %s with an MCM heritage bag (%s).
                Studio quality, warm travel-memory mood, square composition, no text overlay.
                """.formatted(city, country, productName);
    }
}
