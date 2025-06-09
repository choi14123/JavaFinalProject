package JavaFinalProject.six.emotion;

import JavaFinalProject.six.emotion.dto.EmotionType;
import JavaFinalProject.six.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EmotionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private EmotionType emotion;

    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String videoJson; // JSON 형식의 추천 결과 저장

}
