package JavaFinalProject.six.user.dto.request;

import lombok.Getter;

@Getter
public class PasswordChangeRequest {
    private String currentPassword;
    private String newPassword;
}
