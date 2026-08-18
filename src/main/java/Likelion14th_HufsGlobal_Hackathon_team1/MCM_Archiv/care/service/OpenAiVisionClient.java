package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.BusinessException;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
            당신은 명품 가방 상태를 감정하는 전문가입니다. 아래 이미지를 최대한 엄격하고 객관적으로 평가하세요.

            먼저 이미지에서 가방이 명확하게 보이는지 확인하세요.
            - 가방이 안 보이거나, 이미지를 불러올 수 없거나, 가방인지 확신할 수 없으면
              "visible": false 로 응답하고 나머지 점수는 전부 0, comment는 빈 문자열로 채우세요.
              절대 보이지 않는 내용을 지어내지 마세요.
            - 가방이 명확히 보이면 "visible": true 로 하고 아래 기준으로 평가하세요.

            점수 기준 (0~100, 높을수록 좋은 상태). 관대하게 주지 말고 실제로 보이는 손상 기준으로 엄격하게 채점하세요:
            - scratchScore: 스크래치가 거의 없으면 90 이상, 약간 있으면 60~80, 눈에 띄게 많으면 40 이하
            - stainScore: 얼룩이 거의 없으면 90 이상, 약간 있으면 60~80, 눈에 띄면 40 이하
            - wearScore: 마모가 거의 없으면 90 이상, 약간 있으면 60~80, 많이 낡았으면 40 이하

            comment에는 상태에 대한 한국어 한 줄 코멘트를 작성하세요.

            아래 JSON 형식으로만 답하세요:
            {"visible": true, "scratchScore": 0, "stainScore": 0, "wearScore": 0, "comment": "string"}
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
        validateImageAccessible(imageUrl);

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

    private static final String IMAGE_UNREACHABLE_MESSAGE =
            "이미지 URL에 접근할 수 없습니다. 공개적으로 접근 가능한 이미지 주소인지 확인해주세요.";

    private void validateImageAccessible(String imageUrl) {
        try {
            ResponseEntity<Void> response = RestClient.create()
                    .method(HttpMethod.HEAD)
                    .uri(imageUrl)
                    .retrieve()
                    .toBodilessEntity();

            String contentType = response.getHeaders().getContentType() != null
                    ? response.getHeaders().getContentType().toString()
                    : "";

            if (!response.getStatusCode().is2xxSuccessful() || !contentType.startsWith("image/")) {
                throw new BusinessException(ErrorCode.VALIDATION_FAILED, IMAGE_UNREACHABLE_MESSAGE);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, IMAGE_UNREACHABLE_MESSAGE);
        }
    }

    private CareAnalysisResult parseResult(ChatResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "AI 분석 결과가 비어있습니다.");
        }

        String content = response.choices().get(0).message().content();

        CareAnalysisResult result;
        try {
            result = objectMapper.readValue(content, CareAnalysisResult.class);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "AI 분석 결과를 해석할 수 없습니다.");
        }

        if (!result.visible()) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "이미지에서 가방을 확인할 수 없습니다. 접근 가능한 이미지 URL인지 확인해주세요."
            );
        }

        return result;
    }

    public record CareAnalysisResult(boolean visible, int scratchScore, int stainScore, int wearScore, String comment) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record ChatRequest(
            String model,
            List<Message> messages,
            @JsonProperty("response_format") ResponseFormat responseFormat
    ) {
        record Message(String role, List<Content> content) {
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
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
