package JavaFinalProject.six.emotion;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import JavaFinalProject.six.emotion.dto.EmotionType;
import JavaFinalProject.six.emotion.repository.EmotionRepository;


@Component
@RequiredArgsConstructor
public class EmotionInit implements CommandLineRunner {

    private final EmotionRepository emotionRepository;

    @Override
    public void run(String... args) {
        for (EmotionType type : EmotionType.values()) {
            if (emotionRepository.findByEmotionType(type).isEmpty()) {
                Emotion emotion = Emotion.builder()
                        .emotionType(type)
                        .build();
                emotionRepository.save(emotion);
            }
        }
    }
}
