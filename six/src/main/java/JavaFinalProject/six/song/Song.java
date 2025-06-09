package JavaFinalProject.six.song;

import JavaFinalProject.six.emotion.Emotion;
import JavaFinalProject.six.emotion.EmotionHistory;
import JavaFinalProject.six.user.User;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "songs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String artist;

    @ManyToOne
    private User user;

    @ManyToOne
    @JoinColumn(name = "emotion_id", nullable = false)
    private Emotion emotion;

    @ManyToOne
    @JoinColumn(name = "emotion_history_id")
    private EmotionHistory emotionHistory;
}