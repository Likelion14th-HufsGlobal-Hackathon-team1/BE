package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CharmCandidateImageService {

    private static final int CANDIDATE_COUNT = 3;
    private static final String ALLOWED_REFERENCE_IMAGE_HOST = "res.cloudinary.com";

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
                .parallel()
                .mapToObj(i -> buildPrompt(country, city, memo, VARIATION_HINTS.get(i), reference != null))
                .map(prompt -> reference == null
                        ? geminiImageClient.generateImageBase64(prompt)
                        : geminiImageClient.generateImageBase64(prompt, reference.mimeType(), reference.base64Data()))
                .map(imageUploader::upload)
                .toList();
    }

    private ReferenceImage fetchReferenceImage(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty() || !isAllowedReferenceImageUrl(imageUrls.get(0))) {
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

    private boolean isAllowedReferenceImageUrl(String url) {
        try {
            URI uri = URI.create(url);
            String scheme = uri.getScheme();
            return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    && ALLOWED_REFERENCE_IMAGE_HOST.equalsIgnoreCase(uri.getHost());
        } catch (Exception e) {
            return false;
        }
    }

    private record ReferenceImage(String mimeType, String base64Data) {}

    private String buildPrompt(String country, String city, String memo, String variationHint, boolean hasReferenceImage) {
        String memorySection = (memo == null || memo.isBlank())
                ? "Use a warm travel-memory atmosphere appropriate to the city."
                : "Use this travel memory as contextual guidance: " + memo;

        String referenceSection = hasReferenceImage
                ? " Use the attached reference photo only for mood/composition guidance, "
                + "not a literal copy; ignore its aspect ratio and borders; never reproduce "
                + "its text or logos; if it conflicts with the city, the city wins."
                : "";

        return """
                No text/letters/numbers/logos/watermarks anywhere, including signage —
                render neon/signage areas as abstract glowing color blocks, not lettering.
                Fill the entire square canvas edge-to-edge: no border, frame, margin, card,
                badge, medallion, or canvas/print/poster mockup styling.

                Depict %s, %s specifically — real local architecture, streetscape, and
                scenery, not generic country imagery or landmarks borrowed from another
                city. If unsure of a specific landmark, use general local atmosphere
                instead of inventing one.

                %s Reflect it through season, weather, time of day, and mood; never render
                it as text.

                Warm, colorful, polished travel illustration, vivid and specific rather
                than a generic poster. Primarily architecture/scenery; people optional,
                depicted respectfully with no caricatures or stereotypes.

                %s%s
                """.formatted(city, country, memorySection, variationHint, referenceSection);
    }
}
