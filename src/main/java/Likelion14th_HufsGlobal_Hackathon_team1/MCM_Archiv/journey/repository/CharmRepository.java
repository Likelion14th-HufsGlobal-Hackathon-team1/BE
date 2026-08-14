package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.repository;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.entity.Charm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CharmRepository extends JpaRepository<Charm, Long> {

    List<Charm> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Charm> findByIdAndUserId(Long id, Long userId);
}