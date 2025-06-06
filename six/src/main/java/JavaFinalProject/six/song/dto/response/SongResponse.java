package JavaFinalProject.six.song.dto.response;

import JavaFinalProject.six.emotion.Emotion;
import lombok.*;

@Getter
@AllArgsConstructor
public class SongResponse{
    private Long id;
    private String title;
    private String artist;
    private String emotion;
}
