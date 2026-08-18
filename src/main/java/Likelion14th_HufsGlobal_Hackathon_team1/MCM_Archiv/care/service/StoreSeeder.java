package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.entity.Store;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.care.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class StoreSeeder implements ApplicationRunner {

    private final StoreRepository storeRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (storeRepository.count() > 0) {
            return;
        }

        storeRepository.saveAll(List.of(
                Store.of(
                        "MCM 롯데백화점 본점",
                        "서울특별시 중구 남대문로 81 롯데백화점 본점 1F",
                        new BigDecimal("37.564616"),
                        new BigDecimal("126.982065"),
                        "02-772-3198"
                ),
                Store.of(
                        "MCM 롯데백화점 잠실점",
                        "서울특별시 송파구 올림픽로 240 롯데백화점 잠실점 1F",
                        new BigDecimal("37.5126"),
                        new BigDecimal("127.0980"),
                        "02-2143-7205"
                ),
                Store.of(
                        "MCM 하우스 플래그십스토어",
                        "서울특별시 강남구 압구정로 412",
                        new BigDecimal("37.5273"),
                        new BigDecimal("127.0429"),
                        "02-540-1404"
                )
        ));
    }
}
