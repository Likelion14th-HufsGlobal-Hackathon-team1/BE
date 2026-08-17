package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.BusinessException;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.ErrorCode;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.dto.UserUpdateRequest;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.entity.RepresentativeBag;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.entity.User;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.repository.RepresentativeBagRepository;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RepresentativeBagRepository representativeBagRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(String name, String nickname, String email, String loginId, String rawPassword) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "이미 사용 중인 이메일입니다.");
        }

        User user = User.register(name, email, loginId, passwordEncoder.encode(rawPassword), nickname);
        return userRepository.save(user);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public User update(Long userId, UserUpdateRequest request) {
        User user = findById(userId);

        user.updateProfile(request.nickname(), request.profileImage());
        if (request.password() != null) {
            user.changePassword(passwordEncoder.encode(request.password()));
        }

        return user;
    }

    @Transactional
    public void withdraw(Long userId) {
        User user = findById(userId);
        userRepository.delete(user);
    }

    public List<RepresentativeBag> findRepresentativeBags() {
        return representativeBagRepository.findAll();
    }
}
