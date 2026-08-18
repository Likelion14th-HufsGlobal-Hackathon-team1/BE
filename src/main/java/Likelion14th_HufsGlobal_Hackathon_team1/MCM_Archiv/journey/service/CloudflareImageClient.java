package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.BusinessException;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CloudflareImageClient {

    private static final String MODEL = "@cf/black-forest-labs/flux-1-schnell";

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
        CloudflareResponse response = restClient.post()
                .header("Authorization", "Bearer " + apiToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CloudflareRequest(prompt))
                .retrieve()
                .body(CloudflareResponse.class);

        return extractBase64(response);
    }

    private String extractBase64(CloudflareResponse response) {
        if (response == null || response.result() == null || response.result().image() == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "AI 이미지 생성 결과가 비어있습니다.");
        }
        return response.result().image();
    }

    private record CloudflareRequest(String prompt) {}

    private record CloudflareResponse(boolean success, Result result) {
        record Result(String image) {}
    }
}
