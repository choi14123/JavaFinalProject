package JavaFinalProject.six.emotion.dto;

import JavaFinalProject.six.song.YoutubeVideo;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class EmotionHistoryView {
    private EmotionType emotion;
    private LocalDateTime createdAt;
    private List<YoutubeVideo> videos;
    
    public EmotionHistoryView(EmotionType emotion, LocalDateTime createdAt, List<YoutubeVideo> videos) {
        this.emotion = emotion;
        this.createdAt = createdAt;
        this.videos = videos;
    }
}