package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.health;

public record HealthCheckResponse(String status) {

    public static HealthCheckResponse ok() {
        return new HealthCheckResponse("OK");
    }
}
