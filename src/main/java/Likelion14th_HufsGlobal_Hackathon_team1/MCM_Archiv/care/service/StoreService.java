package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.dto.StoreAvailableTimesResponse;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.Store;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.repository.CareReservationRepository;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.repository.StoreRepository;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.BusinessException;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private static final LocalTime OPEN_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(18, 0);
    private static final int SLOT_INTERVAL_MINUTES = 30;

    private final StoreRepository storeRepository;
    private final CareReservationRepository careReservationRepository;

    public List<Store> findAllByDistance(
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        double userLatitude = latitude.doubleValue();
        double userLongitude = longitude.doubleValue();

        return storeRepository.findAll().stream()
                .filter(store ->
                        store.getLatitude() != null
                                && store.getLongitude() != null
                )
                .map(store -> new StoreDistance(
                        store,
                        calculateDistance(
                                userLatitude,
                                userLongitude,
                                store.getLatitude().doubleValue(),
                                store.getLongitude().doubleValue()
                        )
                ))
                .sorted((store1, store2) ->
                        Double.compare(store1.distance(), store2.distance())
                )
                .map(StoreDistance::store)
                .toList();
    }

    private double calculateDistance(
            double lat1,
            double lng1,
            double lat2,
            double lng2
    ) {
        final double earthRadiusKm = 6371.0;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2)
                * Math.sin(lngDistance / 2);

        double clampedA = Math.min(1.0, Math.max(0.0, a));

        double c = 2 * Math.atan2(
                Math.sqrt(clampedA),
                Math.sqrt(1 - clampedA)
        );

        return earthRadiusKm * c;
    }

    private record StoreDistance(
            Store store,
            double distance
    ) {
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
        findById(storeId);

        Set<LocalTime> reservedTimes =
                careReservationRepository
                        .findAllByStoreIdAndReservationDate(storeId, date)
                        .stream()
                        .map(reservation -> reservation.getReservationTime())
                        .collect(Collectors.toSet());

        return new StoreAvailableTimesResponse(
                date,
                createTimeSlots(reservedTimes)
        );
    }

    private List<String> createTimeSlots(Set<LocalTime> reservedTimes) {
        List<String> availableTimes = new ArrayList<>();

        LocalTime currentTime = OPEN_TIME;

        while (!currentTime.isAfter(CLOSE_TIME)) {
            if (!reservedTimes.contains(currentTime)) {
                availableTimes.add(currentTime.toString());
            }

            currentTime = currentTime.plusMinutes(SLOT_INTERVAL_MINUTES);
        }

        return availableTimes;
    }

    public boolean isValidReservationTime(LocalTime reservationTime) {
        if (reservationTime.isBefore(OPEN_TIME)
                || reservationTime.isAfter(CLOSE_TIME)) {
            return false;
        }

        long minutesFromOpen =
                Duration.between(OPEN_TIME, reservationTime).toMinutes();

        return minutesFromOpen % SLOT_INTERVAL_MINUTES == 0;
    }
}
