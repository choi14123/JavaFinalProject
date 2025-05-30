package JavaFinalProject.six.user.controller.user;

import JavaFinalProject.six.user.service.UserService;
import JavaFinalProject.six.user.dto.request.LoginRequest;
import JavaFinalProject.six.user.dto.request.SignUpRequest;
import JavaFinalProject.six.user.dto.response.LoginResponse;
import JavaFinalProject.six.user.dto.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

@Controller
@RequestMapping("/api/public/users")
@RequiredArgsConstructor
public class PublicUserController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @GetMapping("/mypage")
    public String mypage() {
        return "mypage";
    }

    @GetMapping("/password")
    public String password(){
        return "password";
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signup(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse response = userService.register(request);
        return ResponseEntity
                .created(URI.create("/api/private/users/" + response.id()))
                .body(response);
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
