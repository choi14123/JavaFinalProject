package JavaFinalProject.six.emotion;

import JavaFinalProject.six.song.Song;
import lombok.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

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

    private String name;
    
    @OneToMany(mappedBy = "emotion")
    private List<Song> songs = new ArrayList<>();
}