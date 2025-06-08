package JavaFinalProject.six.song;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import JavaFinalProject.six.song.service.YoutubeService;

import java.util.List;

@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
public class YoutubeController {

    private final YoutubeService youtubeService;

    @GetMapping("/recommend")
    public ResponseEntity<List<YoutubeVideo>> search(@RequestParam String emotion) {
        List<YoutubeVideo> results = youtubeService.searchByEmotion(emotion);
        return ResponseEntity.ok(results);
    }
}