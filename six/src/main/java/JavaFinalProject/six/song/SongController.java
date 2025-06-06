package JavaFinalProject.six.song;

import JavaFinalProject.six.song.dto.request.SongRequest;
import JavaFinalProject.six.song.dto.response.SongResponse;
import JavaFinalProject.six.song.service.SongService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @PostMapping
    public ResponseEntity<SongResponse> save(@RequestBody SongRequest request) {
        SongResponse response = songService.save(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/emotion")
    public ResponseEntity<List<SongResponse>> find(@RequestParam String name) {
        List<SongResponse> response = songService.findByEmotion(name);
        //model.addAttribute("songs", songs);
        //model.addAttribute("emotion", name);
        return ResponseEntity.ok(response);
    }
}