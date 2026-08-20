package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.BusinessException;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Base64;

@Component
public class CloudflareImageClient {

    private static final String MODEL = "@cf/stabilityai/stable-diffusion-xl-base-1.0";

    private static final String NEGATIVE_PROMPT =
            "text, letters, words, numbers, writing, caption, label, signage, title, "
            + "typography, watermark, signature, logo, brand mark, frame, border, ring, "
            + "badge, card, racial caricature, ethnic stereotype, offensive imagery";

    private final RestClient restClient;
    private final String apiToken;

    public CloudflareImageClient(@Value("${ai.cloudflare.account-id}") String accountId,
                                  @Value("${ai.cloudflare.api-token}") String apiToken) {
        this.apiToken = apiToken;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.cloudflare.com/client/v4/accounts/" + accountId + "/ai/run/" + MODEL)
                .build();
    }

    public String generateImageBase64(String prompt) {
        byte[] imageBytes = restClient.post()
                .header("Authorization", "Bearer " + apiToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CloudflareRequest(prompt, NEGATIVE_PROMPT))
                .retrieve()
                .body(byte[].class);

        if (imageBytes == null || imageBytes.length == 0) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "AI 이미지 생성 결과가 비어있습니다.");
        }

        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private record CloudflareRequest(
            String prompt,
            @JsonProperty("negative_prompt") String negativePrompt
    ) {}
}
