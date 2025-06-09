package JavaFinalProject.six.user.service;

import JavaFinalProject.six.emotion.repository.EmotionHistoryRepository;
import JavaFinalProject.six.emotion.repository.EmotionRepository;
import JavaFinalProject.six.exception.InvalidPasswordException;
import JavaFinalProject.six.user.Role;
import JavaFinalProject.six.user.User;
import JavaFinalProject.six.user.dto.Provider;
import JavaFinalProject.six.user.repository.UserRepository;
import JavaFinalProject.six.user.dto.request.LoginRequest;
import JavaFinalProject.six.user.dto.request.SignUpRequest;
import JavaFinalProject.six.user.dto.response.LoginResponse;
import JavaFinalProject.six.user.dto.response.SignUpResponse;
import JavaFinalProject.six.exception.UserAlreadyExistsException;
import JavaFinalProject.six.security.jwt.JwtTokenProvider;
import JavaFinalProject.six.util.RandomNickname;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmotionHistoryRepository emotionHistoryRepository;

    @Lazy
    private final JwtTokenProvider jwtTokenProvider;

    public SignUpResponse register(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("이미 존재하는 이메일입니다.");
        }

        // 닉네임이 없을 경우 랜덤 닉네임 생성
        String nickname = request.getNickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = RandomNickname.generateRandomNickname();
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickname(nickname)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        userRepository.save(user);

        return new SignUpResponse(user.getId(), user.getEmail(), user.getName(), nickname);
    }

    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(user.getEmail());

        return new LoginResponse(token);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));
    }

    public void updateUser(String email, String name, String nickname) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.changeProfile(name, nickname);
        userRepository.save(user);
    }

    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 소셜 로그인 사용자는 비밀번호 변경 불가
        if (user.getProvider() ==  Provider.NAVER) {
            throw new IllegalStateException("소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다.");
        }

        // 현재 비밀번호와 새 비밀번호가 같으면 예외
        if (currentPassword.equals(newPassword)) {
            throw new IllegalArgumentException("현재 비밀번호와 새로운 비밀번호가 같습니다.");
        }

        // 현재 비밀번호 일치 여부 검증
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호 변경
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodedNewPassword);
        userRepository.save(user);
    }

    @Transactional
    public void userDelete(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        emotionHistoryRepository.deleteByUserId(user.getId());  // 자식 테이블 먼저 삭제
        userRepository.deleteById(user.getId());                // 이후 부모 삭제
        userRepository.delete(user);
    }

    @Transactional
    public String handleOAuth2Login(OAuth2User oAuth2User) {
        Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
        String name = (String) response.get("name");
        String email = (String) response.get("email");


        Optional<User> optionalUser = userRepository.findByEmail(email);

        User user = optionalUser.orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .name(name)
                    .nickname(RandomNickname.generateRandomNickname())
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .role(Role.USER)
                    .provider(Provider.NAVER)
                    .build();
            return userRepository.save(newUser);
        });
        return jwtTokenProvider.createToken(user.getEmail());
    }
}