package JavaFinalProject.six.song.repository;

import JavaFinalProject.six.song.Song;
import JavaFinalProject.six.emotion.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByEmotion(Emotion emotion);
}