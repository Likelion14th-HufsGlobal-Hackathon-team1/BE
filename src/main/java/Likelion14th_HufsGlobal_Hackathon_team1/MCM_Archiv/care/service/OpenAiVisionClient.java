package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.BusinessException;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * care_reports AI 진단용 — 이미지 "생성"이 아니라 "분석"이라 gpt-image 계열의
 * TPM 0 제한(조직 인증 필요)에 안 걸리는 일반 채팅/비전 모델을 사용한다.
 */
@Component
public class OpenAiVisionClient {

    private static final String MODEL = "gpt-4.1-mini";

    private static final String PROMPT = """
            이 명품 가방 이미지를 보고 상태를 분석해줘.
            scratchScore(스크래치), stainScore(얼룩), wearScore(마모)를 각각 0~100 사이 정수로 평가하고,
            comment에 상태에 대한 한국어 한 줄 코멘트를 작성해서 아래 JSON 형식으로만 답해:
            {"scratchScore": 0, "stainScore": 0, "wearScore": 0, "comment": "string"}
            """;

    private final RestClient restClient;
    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAiVisionClient(@Value("${ai.openai.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .build();
    }

    public CareAnalysisResult analyzeBagImage(String imageUrl) {
        ChatRequest request = new ChatRequest(
                MODEL,
                List.of(new ChatRequest.Message(
                        "user",
                        List.of(
                                new ChatRequest.Content("text", PROMPT, null),
                                new ChatRequest.Content("image_url", null, new ChatRequest.ImageUrl(imageUrl))
                        )
                )),
                new ChatRequest.ResponseFormat("json_object")
        );

        ChatResponse response = restClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ChatResponse.class);

        return parseResult(response);
    }

    private CareAnalysisResult parseResult(ChatResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "AI 분석 결과가 비어있습니다.");
        }

        String content = response.choices().get(0).message().content();

        try {
            return objectMapper.readValue(content, CareAnalysisResult.class);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "AI 분석 결과를 해석할 수 없습니다.");
        }
    }

    public record CareAnalysisResult(int scratchScore, int stainScore, int wearScore, String comment) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record ChatRequest(
            String model,
            List<Message> messages,
            @JsonProperty("response_format") ResponseFormat responseFormat
    ) {
        record Message(String role, List<Content> content) {
        }

        record Content(String type, String text, @JsonProperty("image_url") ImageUrl imageUrl) {
        }

        record ImageUrl(String url) {
        }

        record ResponseFormat(String type) {
        }
    }

    private record ChatResponse(List<Choice> choices) {
        record Choice(Message message) {
        }

        record Message(String content) {
        }
    }
}
