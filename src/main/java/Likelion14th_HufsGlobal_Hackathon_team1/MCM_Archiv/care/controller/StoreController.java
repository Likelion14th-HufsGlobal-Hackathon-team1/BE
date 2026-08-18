package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.controller;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto.StoreAvailableTimesResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto.StoreListResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public StoreListResponse findAll(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng
    ) {
        return StoreListResponse.from(
                storeService.findAllByDistance(lat, lng)
        );
    }

    @GetMapping("/{storeId}/available-times")
    public StoreAvailableTimesResponse findAvailableTimes(
            @PathVariable Long storeId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return storeService.findAvailableTimes(storeId, date);
    }
}
