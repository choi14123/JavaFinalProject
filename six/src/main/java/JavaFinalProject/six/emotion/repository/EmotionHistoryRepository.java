package JavaFinalProject.six.emotion.repository;

import JavaFinalProject.six.emotion.EmotionHistory;
import JavaFinalProject.six.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionHistoryRepository extends JpaRepository<EmotionHistory, Long> {
    List<EmotionHistory> findByUser(User user);

	List<EmotionHistory> findByUserOrderByCreatedAtDesc(User user);

    void deleteByUserId(Long id);
}