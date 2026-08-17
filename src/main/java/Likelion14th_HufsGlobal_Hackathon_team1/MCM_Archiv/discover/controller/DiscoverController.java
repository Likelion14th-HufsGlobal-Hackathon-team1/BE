package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.controller;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.dto.DiscoverStyleListResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.service.DiscoverService;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DiscoverController {

    private final DiscoverService discoverService;

    @GetMapping("/discover")
    public DiscoverStyleListResponse findAll(@LoginUser Long userId) {
        return DiscoverStyleListResponse.from(discoverService.findAllStyles());
    }
}
