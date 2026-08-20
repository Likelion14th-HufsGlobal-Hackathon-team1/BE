package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.BusinessException;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Component
public class GeminiImageClient {

    private static final String MODEL = "gemini-2.5-flash-image";
    private static final double TEMPERATURE = 1.3;

    private final RestClient restClient;
    private final String apiKey;

    public GeminiImageClient(@Value("${ai.gemini.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/" + MODEL + ":generateContent")
                .build();
    }

    public String generateImageBase64(String prompt) {
        GeminiRequest request = GeminiRequest.of(prompt);

        GeminiResponse response = restClient.post()
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(GeminiResponse.class);

        return extractBase64(response);
    }

    private String extractBase64(GeminiResponse response) {
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "AI 이미지 생성 결과가 비어있습니다.");
        }
        return response.candidates().get(0).content().parts().stream()
                .map(GeminiResponse.Part::inlineData)
                .filter(Objects::nonNull)
                .map(GeminiResponse.InlineData::data)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_ERROR, "AI 이미지 데이터를 찾을 수 없습니다."));
    }

    private record GeminiRequest(List<ReqContent> contents, GenerationConfig generationConfig) {
        static GeminiRequest of(String prompt) {
            return new GeminiRequest(
                    List.of(new ReqContent(List.of(new ReqPart(prompt)))),
                    new GenerationConfig(List.of("IMAGE"), TEMPERATURE)
            );
        }
        record ReqContent(List<ReqPart> parts) {}
        record ReqPart(String text) {}
        record GenerationConfig(List<String> responseModalities, Double temperature) {}
    }

    private record GeminiResponse(List<Candidate> candidates) {
        record Candidate(Content content) {}
        record Content(List<Part> parts) {}
        record Part(String text, InlineData inlineData) {}
        record InlineData(String mimeType, String data) {}
    }
}
