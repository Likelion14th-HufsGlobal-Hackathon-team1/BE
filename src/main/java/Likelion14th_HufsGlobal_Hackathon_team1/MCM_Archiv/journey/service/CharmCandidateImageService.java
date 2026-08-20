package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CharmCandidateImageService {

    private static final int CANDIDATE_COUNT = 3;

    private static final List<String> VARIATION_HINTS = List.of(
            "Center the composition on the city's single most iconic landmark, in bright "
                    + "daytime lighting.",
            "Center the composition on a characteristic street or neighborhood scene rather "
                    + "than one landmark, in warm golden-hour lighting.",
            "Center the composition on a natural or waterfront element (park, river, "
                    + "coastline, or greenery) typical of the city if one exists, in a "
                    + "cooler evening or dusk mood."
    );

    private final GeminiImageClient geminiImageClient;
    private final CloudinaryImageUploader imageUploader;
    private final RestClient referenceImageFetcher = RestClient.create();

    public List<String> generateCandidateImageUrls(String country, String city, String memo, List<String> imageUrls) {
        ReferenceImage reference = fetchReferenceImage(imageUrls);

        return IntStream.range(0, CANDIDATE_COUNT)
                .mapToObj(i -> buildPrompt(country, city, memo, VARIATION_HINTS.get(i), reference != null))
                .map(prompt -> reference == null
                        ? geminiImageClient.generateImageBase64(prompt)
                        : geminiImageClient.generateImageBase64(prompt, reference.mimeType(), reference.base64Data()))
                .map(imageUploader::upload)
                .toList();
    }

    private ReferenceImage fetchReferenceImage(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return null;
        }
        try {
            ResponseEntity<byte[]> response = referenceImageFetcher.get()
                    .uri(imageUrls.get(0))
                    .retrieve()
                    .toEntity(byte[].class);

            MediaType contentType = response.getHeaders().getContentType();
            byte[] body = response.getBody();
            if (contentType == null || !"image".equals(contentType.getType()) || body == null) {
                return null;
            }
            return new ReferenceImage(contentType.toString(), Base64.getEncoder().encodeToString(body));
        } catch (Exception e) {
            return null;
        }
    }

    private record ReferenceImage(String mimeType, String base64Data) {}

    private String buildPrompt(String country, String city, String memo, String variationHint, boolean hasReferenceImage) {
        String memorySection = (memo == null || memo.isBlank())
                ? "Use a warm travel-memory atmosphere appropriate to the city."
                : "Use this travel memory as contextual guidance: " + memo;

        String referenceSection = hasReferenceImage
                ? """

                REFERENCE IMAGE:
                Use the attached reference image only as supporting visual guidance for
                mood, scenery, or composition when relevant. Do not copy it literally. If
                it conflicts with the specified city, prioritize the city identity above.
                Never reproduce any text, signage, logos, or branded elements visible in
                the reference image.
                """
                : "";

        return ("""
                CRITICAL CONSTRAINTS:
                The image must contain absolutely no text or typography of any kind.
                No letters, words, numbers, captions, labels, readable or unreadable
                writing, pseudo-text, fake lettering, storefront lettering, street-sign
                text, neon-sign text, posters, banners, watermarks, signatures, or
                logo-like marks. Avoid branded storefronts, recognizable commercial
                signage, and trademark-like visual branding.

                FULL-BLEED COMPOSITION:
                Create a square illustration that fills the entire canvas edge to edge.
                The illustrated scene itself must reach all four edges. No border, frame,
                margin, ring, card, badge, sticker, postcard, medallion, icon container,
                or framed-picture composition. Do not show the illustration as an object
                placed on another background.

                LOCATION:
                Depict a scene specifically recognizable as %s, %s.
                Prioritize architecture, streetscape, landmarks, public spaces, natural
                scenery, urban character, terrain, vegetation, and waterways strongly
                associated with this specific city — not just generic imagery of the
                country.
                Do not substitute landmarks or visual features from another city simply
                because they are famous within the same country. If uncertain about a
                specific landmark, prefer characteristic local architecture, streetscape,
                or atmosphere rather than inventing or borrowing a landmark from another
                city.
                Create a cohesive city scene rather than a single isolated landmark icon.

                TRAVEL MEMORY:
                %s
                Reflect it naturally through the scene, season, weather, time of day,
                activity, composition, and colors when appropriate. Never display, quote,
                translate, spell out, or otherwise render this memory as text.

                STYLE:
                Warm, colorful, polished travel illustration with clean composition and
                rich environmental detail. The final image should feel like a vivid travel
                memory, not a generic city poster. No logos or brand marks.

                PEOPLE:
                Focus primarily on architecture, landmarks, nature, streets, and scenery.
                People are optional and secondary. If people appear, portray them
                naturally, respectfully, and neutrally — no racial or ethnic caricatures,
                exaggerated cultural stereotypes, or offensive depictions.

                VARIATION:
                %s
                """ + referenceSection).formatted(city, country, memorySection, variationHint);
    }
}
