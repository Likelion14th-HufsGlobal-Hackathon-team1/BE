package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.CareReport;

import java.time.Instant;

public record CareReportResponse(
        Long careId,
        Integer totalScore,
        Integer scratchScore,
        Integer stainScore,
        Integer wearScore,
        String aiComment,
        Instant analyzedAt
) {

    public static CareReportResponse from(CareReport report) {
        return new CareReportResponse(
                report.getId(),
                report.getTotalScore(),
                report.getScratchScore(),
                report.getStainScore(),
                report.getWearScore(),
                report.getAiComment(),
                report.getAnalyzedAt()
        );
    }
}