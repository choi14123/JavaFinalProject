package JavaFinalProject.six.emotion;

import JavaFinalProject.six.emotion.dto.EmotionType;
import JavaFinalProject.six.song.Song;
import JavaFinalProject.six.user.User;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
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

    @OneToMany(mappedBy = "emotionHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Song> songs = new ArrayList<>();
}
