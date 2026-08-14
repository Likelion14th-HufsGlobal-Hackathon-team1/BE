package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record JourneyVerifyRequest(
        @NotNull Long productId,
        @NotBlank String country,
        @NotBlank String city,
        @Size(max = 500) String memo,
        @NotNull LocalDate travelDate,
        List<String> imageUrls
) {
}