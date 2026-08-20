package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto;

import jakarta.validation.constraints.NotNull;

public record CharmPositionUpdateRequest(

        @NotNull
        Double positionX,

        @NotNull
        Double positionY,

        @NotNull
        Double rotation,

        @NotNull
        Double scale

) {
}
