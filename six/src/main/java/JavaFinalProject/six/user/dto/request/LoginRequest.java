package JavaFinalProject.six.user.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Valid
public record LoginRequest(
        @NotBlank @Email String email,

        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}\\[\\]:;<>,.?~\\\\/-]).{8,}$",
                message = "비밀번호는 영문자, 숫자, 특수문자를 포함해 8자 이상이어야 합니다."
        )
        @NotBlank String password
) {}
