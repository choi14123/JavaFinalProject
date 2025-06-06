package JavaFinalProject.six.emotion;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmotionInit implements CommandLineRunner {

    private final EmotionRepository emotionRepository;

    @Override
    public void run(String... args) {
        for (EmotionType type : EmotionType.values()) {
            if (emotionRepository.findByName(type.name()) == null) {
                Emotion emotion = new Emotion();
                emotion.setName(type.name());
                emotionRepository.save(emotion);
            }
        }
    }
}