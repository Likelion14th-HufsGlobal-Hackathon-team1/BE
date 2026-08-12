package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error;

public record ErrorResponse(ErrorDetail error) {

    public record ErrorDetail(String code, String message) {
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(new ErrorDetail(errorCode.name(), message));
    }
}
