package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health-check")
    public HealthCheckResponse healthCheck() {
        return HealthCheckResponse.ok();
    }
}
