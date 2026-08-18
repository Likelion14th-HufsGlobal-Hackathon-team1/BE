package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto.CareReservationRequest;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto.CareReservationResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.CareReservation;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.repository.CareReservationRepository;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.BusinessException;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareReservationService {

    private final CareReservationRepository careReservationRepository;
    private final CareReportService careReportService;
    private final StoreService storeService;

    @Transactional
    public CareReservationResponse createReservation(
            Long userId,
            CareReservationRequest request
    ) {
        careReportService.findReport(userId, request.careId());

        storeService.findById(request.storeId());

        if (!storeService.isValidReservationTime(request.reservationTime())) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "예약 가능한 시간은 08:00부터 18:00까지 30분 단위입니다."
            );
        }

        if (careReservationRepository.existsByCareId(request.careId())) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "이미 해당 진단 결과로 예약이 존재합니다."
            );
        }

        if (careReservationRepository
                .existsByStoreIdAndReservationDateAndReservationTime(
                        request.storeId(),
                        request.reservationDate(),
                        request.reservationTime()
                )) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "이미 예약된 시간입니다."
            );
        }

        CareReservation reservation = CareReservation.create(
                request.careId(),
                request.storeId(),
                request.reservationDate(),
                request.reservationTime()
        );

        return CareReservationResponse.from(
                careReservationRepository.save(reservation)
        );
    }
}
