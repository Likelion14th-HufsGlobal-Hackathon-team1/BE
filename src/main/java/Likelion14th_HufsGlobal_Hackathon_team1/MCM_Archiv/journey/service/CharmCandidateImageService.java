package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CharmCandidateImageService {

    private static final int CANDIDATE_COUNT = 3;

    private final GeminiImageClient geminiImageClient;
    private final CloudinaryImageUploader imageUploader;

    public List<String> generateCandidateImageUrls(String country, String city, String memo) {
        String prompt = buildPrompt(country, city, memo);

        return IntStream.range(0, CANDIDATE_COUNT)
                .mapToObj(i -> geminiImageClient.generateImageBase64(prompt))
                .map(imageUploader::upload)
                .toList();
    }

    private String buildPrompt(String country, String city, String memo) {
        String memoLine = (memo == null || memo.isBlank()) ? "a memorable trip" : memo;

        return """
                CRITICAL CONSTRAINTS:
                The image must contain absolutely no text or typography of any kind.
                No letters, words, numbers, captions, labels, readable or unreadable
                writing, pseudo-text, fake lettering, storefront lettering, street-sign
                text, neon-sign text, posters, banners, watermarks, signatures, or
                logo-like marks.

                FULL-BLEED COMPOSITION:
                Create a square illustration that fills the entire canvas edge to edge.
                The illustrated scene itself must reach all four edges. No border, frame,
                margin, ring, card, badge, sticker, postcard, medallion, icon container,
                or framed-picture composition. Do not show the illustration as an object
                placed on another background.

                LOCATION:
                Depict a scene specifically recognizable as %s, %s.
                Prioritize architecture, streetscape, landmarks, natural scenery, urban
                character, terrain, vegetation, and waterways strongly associated with
                this specific city — not just generic imagery of the country.
                Do not substitute landmarks or visual features from another city simply
                because they are famous within the same country. If uncertain about a
                specific landmark, prefer characteristic local architecture, streetscape,
                or atmosphere rather than inventing or borrowing a landmark from another
                city.
                Create a cohesive city scene rather than a single isolated landmark icon.

                TRAVEL MEMORY:
                Use this travel memory as contextual guidance: %s
                Reflect it naturally through the scene, season, weather, time of day,
                activity, composition, and colors when appropriate. Never display, quote,
                translate, spell out, or otherwise render this memory as text.

                STYLE:
                Warm, colorful, polished travel illustration with clean composition and
                rich environmental detail. No logos or brand marks.

                PEOPLE:
                Focus primarily on architecture, landmarks, nature, streets, and scenery.
                People are optional and secondary. If people appear, portray them
                naturally, respectfully, and neutrally — no racial or ethnic caricatures,
                exaggerated cultural stereotypes, or offensive depictions.
                """.formatted(city, country, memoLine);
    }
}
