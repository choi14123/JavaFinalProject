package JavaFinalProject.six.emotion;

import JavaFinalProject.six.emotion.dto.EmotionType;
import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "emotions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Emotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // Enum을 문자열로 저장
    @Column(nullable = false, unique = true)
    private EmotionType emotionType;

    private String description; // 설명 등 추가 필드
}