package JavaFinalProject.six.song;

import JavaFinalProject.six.emotion.Emotion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByEmotion(Emotion emotion);
}
