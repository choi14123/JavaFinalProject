package JavaFinalProject.six.emotion.repository;

import JavaFinalProject.six.emotion.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {
    List<Emotion> findByName(String string);

}