package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.entity.DiscoverStyle;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.discover.repository.DiscoverStyleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscoverService {

    private final DiscoverStyleRepository discoverStyleRepository;

    public List<DiscoverStyle> findAllStyles() {
        return discoverStyleRepository.findAll();
    }
}
