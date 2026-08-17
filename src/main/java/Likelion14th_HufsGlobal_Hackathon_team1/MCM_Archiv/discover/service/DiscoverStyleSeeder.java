package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.entity.DiscoverStyle;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.repository.DiscoverStyleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * GET /discover는 AI 추천 없이 고정 시드 데이터를 내려준다 (API계약서 §5).
 * 테스트 프로필에서는 불필요한 데이터라 제외.
 */
@Component
@RequiredArgsConstructor
@Profile("!test")
public class DiscoverStyleSeeder implements ApplicationRunner {

    private final DiscoverStyleRepository discoverStyleRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (discoverStyleRepository.count() > 0) {
            return;
        }

        discoverStyleRepository.saveAll(List.of(
                DiscoverStyle.of("https://cdn.example.com/discover/style-1.png", "미니멀 오피스룩"),
                DiscoverStyle.of("https://cdn.example.com/discover/style-2.png", "캐주얼 위켄드룩"),
                DiscoverStyle.of("https://cdn.example.com/discover/style-3.png", "트래블 무드룩")
        ));
    }
}
