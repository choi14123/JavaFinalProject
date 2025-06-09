package JavaFinalProject.six.emotion.repository;

import JavaFinalProject.six.emotion.Emotion;
import JavaFinalProject.six.emotion.dto.EmotionType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionRepository extends JpaRepository<Emotion, Long> {
    Optional<Emotion> findByEmotionType(EmotionType emotionType);
}