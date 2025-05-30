package JavaFinalProject.six.user.service;

import JavaFinalProject.six.exception.InvalidPasswordException;
import JavaFinalProject.six.user.Role;
import JavaFinalProject.six.user.User;
import JavaFinalProject.six.user.repository.UserRepository;
import JavaFinalProject.six.user.dto.request.LoginRequest;
import JavaFinalProject.six.user.dto.request.SignUpRequest;
import JavaFinalProject.six.user.dto.response.LoginResponse;
import JavaFinalProject.six.user.dto.response.SignUpResponse;
import JavaFinalProject.six.exception.UserAlreadyExistsException;
import JavaFinalProject.six.security.jwt.JwtTokenProvider;
import JavaFinalProject.six.util.RandomNickname;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
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

        if (currentPassword.equals(newPassword)) {
            throw new IllegalArgumentException("현재 비밀번호와 새로운 비밀번호가 같습니다.");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }



        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodedNewPassword);
        userRepository.save(user);
    }

    public void userDelete(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        userRepository.delete(user);
    }
}