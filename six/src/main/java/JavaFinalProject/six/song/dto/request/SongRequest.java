package JavaFinalProject.six.song.dto.request;

import JavaFinalProject.six.emotion.Emotion;
import lombok.*;

@Getter
public class SongRequest {
    private String title;
    private String artist;
    private String emotion;
}