package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto.StoreAvailableTimesResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.Store;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.repository.StoreRepository;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.BusinessException;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private static final List<String> MOCK_AVAILABLE_TIMES =
            List.of("14:30", "15:00", "16:00");

    private final StoreRepository storeRepository;

    public List<Store> findAll() {
        return storeRepository.findAll();
    }

    public Store findById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.NOT_FOUND,
                                "매장을 찾을 수 없습니다."
                        )
                );
    }

    public StoreAvailableTimesResponse findAvailableTimes(
            Long storeId,
            LocalDate date
    ) {
        storeRepository.findById(storeId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.NOT_FOUND,
                                "매장을 찾을 수 없습니다."
                        )
                );

        return new StoreAvailableTimesResponse(
                date,
                MOCK_AVAILABLE_TIMES
        );
    }
}
