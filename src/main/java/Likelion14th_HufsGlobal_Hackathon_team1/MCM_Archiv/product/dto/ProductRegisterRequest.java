package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ProductRegisterRequest(

        @NotBlank
        String productCode,

        @NotBlank
        String productName,

        @NotNull
        LocalDate purchaseDate

) {
}
