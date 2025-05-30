package JavaFinalProject.six.user.dto.response;

import JavaFinalProject.six.user.User;

public record UserResponse(Long id, String email, String nickname) {
    public UserResponse(User user) {
        this(user.getId(), user.getEmail(), user.getNickname());
    }
}
