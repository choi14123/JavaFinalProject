package JavaFinalProject.six.emotion.dto;

import JavaFinalProject.six.song.Song;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class EmotionHistoryView {

    private EmotionType emotion;
    private LocalDateTime createdAt;
    private List<Song> songs;

    public EmotionHistoryView(EmotionType emotion, LocalDateTime createdAt, List<Song> songs) {
        this.emotion = emotion;
        this.createdAt = createdAt;
        this.songs = songs;
    }
}