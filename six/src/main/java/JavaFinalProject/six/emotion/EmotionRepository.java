package JavaFinalProject.six.emotion;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {
    Emotion findByName(String emotion);
}