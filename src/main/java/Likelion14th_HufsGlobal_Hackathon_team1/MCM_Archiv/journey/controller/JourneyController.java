package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.controller;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.security.LoginUser;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto.JourneyVerifyRequest;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.dto.JourneyVerifyResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.service.JourneyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/journeys")
@RequiredArgsConstructor
public class JourneyController {

    private final JourneyService journeyService;

    @PostMapping
    public JourneyVerifyResponse createJourney(
            @LoginUser Long userId,
            @Valid @RequestBody JourneyVerifyRequest request
    ) {
        return journeyService.verifyJourney(userId, request);
    }
}