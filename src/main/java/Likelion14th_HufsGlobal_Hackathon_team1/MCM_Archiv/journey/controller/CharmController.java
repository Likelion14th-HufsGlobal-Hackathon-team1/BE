package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.controller;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.security.LoginUser;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto.CharmCreateRequest;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto.CharmCreateResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto.CharmDetailResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto.CharmListResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.service.CharmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/charms")
@RequiredArgsConstructor
public class CharmController {

    private final CharmService charmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CharmCreateResponse createCharm(
            @LoginUser Long userId,
            @Valid @RequestBody CharmCreateRequest request
    ) {
        return charmService.createCharm(userId, request);
    }

    @GetMapping
    public CharmListResponse findCharms(@LoginUser Long userId) {
        return charmService.findCharms(userId);
    }

    @GetMapping("/{charmId}")
    public CharmDetailResponse findCharm(@LoginUser Long userId, @PathVariable Long charmId) {
        return charmService.findCharm(userId, charmId);
    }
}