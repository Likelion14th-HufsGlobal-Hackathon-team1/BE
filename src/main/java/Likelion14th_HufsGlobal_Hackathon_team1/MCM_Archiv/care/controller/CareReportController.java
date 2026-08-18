package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.controller;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto.CareReportCreateRequest;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto.CareReportListResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto.CareReportResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.service.CareReportService;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.security.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/care/reports")
@RequiredArgsConstructor
public class CareReportController {

    private final CareReportService careReportService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CareReportResponse createReport(
            @LoginUser Long userId,
            @Valid @RequestBody CareReportCreateRequest request
    ) {
        return careReportService.createReport(userId, request);
    }

    @GetMapping("/{careId}")
    public CareReportResponse findReport(@LoginUser Long userId, @PathVariable Long careId) {
        return careReportService.findReport(userId, careId);
    }

    @GetMapping
    public CareReportListResponse findAll(@LoginUser Long userId) {
        return careReportService.findAllReports(userId);
    }
}