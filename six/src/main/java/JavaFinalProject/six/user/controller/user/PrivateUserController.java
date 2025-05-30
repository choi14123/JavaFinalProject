package JavaFinalProject.six.user.controller.user;

import JavaFinalProject.six.user.User;
import JavaFinalProject.six.user.dto.request.PasswordChangeRequest;
import JavaFinalProject.six.user.service.UserService;
import JavaFinalProject.six.user.dto.response.UserInfoResponse;
import JavaFinalProject.six.security.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/private/users")
@RequiredArgsConstructor
public class PrivateUserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPage(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        extractEmailFromToken(authorizationHeader);

        String token = authorizationHeader.replace("Bearer ", "");

        try {
            String email = jwtTokenProvider.getEmail(token); // 여기서 예외 나면 위에서 걸러짐
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
            }

            UserInfoResponse response = new UserInfoResponse(
                    user.getEmail(),
                    user.getName(),
                    user.getNickname()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }

    @PostMapping("/mypage")
    public ResponseEntity<?> updateMyPage(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody Map<String, String> updateRequest) {

        extractEmailFromToken(authorizationHeader);

        String token = authorizationHeader.replace("Bearer ", "");

        String email = jwtTokenProvider.getEmail(token);

        try {
            String name = updateRequest.get("name");
            String nickname = updateRequest.get("nickname");

            userService.updateUser(email, name, nickname);
            return ResponseEntity.ok("사용자 정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("/password")
    public ResponseEntity<?> changePassword(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody PasswordChangeRequest request) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("유효한 인증 토큰이 필요합니다.");
        }

        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmail(token);

        userService.changePassword(email, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }


    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser( @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        extractEmailFromToken(authorizationHeader);

        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmail(token);
        userService.userDelete(email);

        return ResponseEntity.ok("사용자가 성공적으로 삭제되었습니다.");
    }

    private String extractEmailFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("인증 토큰이 없습니다.");
        }
        String token = authHeader.replace("Bearer ", "");
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("JWT 토큰이 유효하지 않거나 만료되었습니다.");
        }
        return jwtTokenProvider.getEmail(token);
    }

}