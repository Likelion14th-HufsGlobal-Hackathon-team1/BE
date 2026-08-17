package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.controller;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto.CareReservationRequest;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto.CareReservationResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.service.CareReservationService;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.security.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/care/reservations")
@RequiredArgsConstructor
public class CareReservationController {

    private final CareReservationService careReservationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CareReservationResponse createReservation(
            @LoginUser Long userId,
            @Valid @RequestBody CareReservationRequest request
    ) {
        return careReservationService.createReservation(userId, request);
    }
}
