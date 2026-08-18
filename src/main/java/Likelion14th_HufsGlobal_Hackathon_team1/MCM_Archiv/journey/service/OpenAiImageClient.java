package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.BusinessException;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;


@Component
public class OpenAiImageClient {

    private static final String MODEL = "gpt-image-2";
    private static final String SIZE = "1024x1024";

    private final RestClient restClient;
    private final String apiKey;

    public OpenAiImageClient(@Value("${ai.openai.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1/images/generations")
                .build();
    }

    public String generateImageBase64(String prompt) {
        OpenAiRequest request = new OpenAiRequest(MODEL, prompt, SIZE, 1);

        OpenAiResponse response = restClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(OpenAiResponse.class);

        return extractBase64(response);
    }

    private String extractBase64(OpenAiResponse response) {
        if (response == null || response.data() == null || response.data().isEmpty()) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "AI 이미지 생성 결과가 비어있습니다.");
        }

        String base64 = response.data().get(0).b64Json();
        if (base64 == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "AI 이미지 데이터를 찾을 수 없습니다.");
        }
        return base64;
    }

    private record OpenAiRequest(String model, String prompt, String size, Integer n) {}

    private record OpenAiResponse(List<ImageData> data) {
        record ImageData(@JsonProperty("b64_json") String b64Json) {}
    }
}