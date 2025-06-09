package JavaFinalProject.six.emotion;

import JavaFinalProject.six.emotion.repository.EmotionHistoryRepository;
import JavaFinalProject.six.user.User;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class EmotionHistoryService {
    private final EmotionHistoryRepository emotionHistoryRepository;

    public void save(EmotionHistory history) {
        System.out.println(history);
        emotionHistoryRepository.save(history);
    }

    public List<EmotionHistory> findByUserOrderByCreatedAtDesc(User user) {
        return emotionHistoryRepository.findByUserOrderByCreatedAtDesc(user);
    }
}
