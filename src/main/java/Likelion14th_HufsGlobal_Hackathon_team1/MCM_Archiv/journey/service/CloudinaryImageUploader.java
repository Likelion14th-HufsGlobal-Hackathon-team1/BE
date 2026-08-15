package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.BusinessException;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class CloudinaryImageUploader {

    private final RestClient restClient;
    private final String uploadPreset;

    public CloudinaryImageUploader(@Value("${cloudinary.cloud-name}") String cloudName,
                                   @Value("${cloudinary.upload-preset}") String uploadPreset) {
        this.uploadPreset = uploadPreset;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload")
                .build();
    }

    public String upload(String base64PngImage) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", "data:image/png;base64," + base64PngImage);
        body.add("upload_preset", uploadPreset);

        CloudinaryResponse response = restClient.post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(CloudinaryResponse.class);

        if (response == null || response.secureUrl() == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "이미지 업로드에 실패했습니다.");
        }
        return response.secureUrl();
    }

    private record CloudinaryResponse(@JsonProperty("secure_url") String secureUrl) {}
}
