package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto;

import java.time.LocalDate;
import java.util.List;

public record StoreAvailableTimesResponse(
        LocalDate date,
        List<String> availableTimes
) {
}
