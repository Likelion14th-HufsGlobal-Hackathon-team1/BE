package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CareReportCreateRequest(
        @NotNull Long productId,
        @NotBlank String imageUrl
) {
}