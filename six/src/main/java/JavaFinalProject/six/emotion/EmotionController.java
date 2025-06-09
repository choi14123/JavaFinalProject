package JavaFinalProject.six.emotion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import JavaFinalProject.six.emotion.dto.EmotionHistoryView;
import JavaFinalProject.six.emotion.dto.EmotionType;
import JavaFinalProject.six.emotion.repository.EmotionHistoryRepository;
import JavaFinalProject.six.song.YoutubeVideo;
import JavaFinalProject.six.song.service.YoutubeService;
import JavaFinalProject.six.user.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/emotion")
@RequiredArgsConstructor
public class EmotionController {

    private final YoutubeService youtubeService;
    private final EmotionHistoryRepository emotionHistoryRepository;

    @GetMapping("/select")
    public String showEmotionPage(Model model) {
        model.addAttribute("emotions", EmotionType.values());
        return "select";
    }

    @PostMapping("/select")
    public String handleEmotionSelection(@RequestParam("emotionType") EmotionType emotionType) {
        return "redirect:result?emotion=" + emotionType.name();
    }

    @GetMapping("/result")
    public String showEmotionResult(@RequestParam("emotion") EmotionType emotionType,
                                     @AuthenticationPrincipal User user, Model model) throws Exception {
        String query = getSearchQueryByEmotion(emotionType);
        Map<String, List<YoutubeVideo>> videos = youtubeService.searchByEmotionSeparated(query);

        ObjectMapper mapper = new ObjectMapper();
        List<YoutubeVideo> allVideos = new ArrayList<>();
        allVideos.addAll(videos.get("singles"));
        allVideos.addAll(videos.get("playlists"));
        String videoJson = mapper.writeValueAsString(allVideos);

        model.addAttribute("emotion", emotionType);
        model.addAttribute("singles", videos.get("singles"));
        model.addAttribute("playlists", videos.get("playlists"));
        model.addAttribute("videoJson", videoJson);

        return "result";
    }

    @PostMapping("/save")
    public String saveEmotionResult(@RequestParam("emotion") EmotionType emotionType,
                                    @RequestParam("videoJson") String videoJson,
                                    @AuthenticationPrincipal User user) {
        EmotionHistory history = EmotionHistory.builder()
                .user(user)
                .emotion(emotionType)
                .createdAt(LocalDateTime.now())
                .videoJson(videoJson)
                .build();

        emotionHistoryRepository.save(history);
        return "redirect:/api/emotion/history";
    }

    @GetMapping("/history")
    public String showEmotionHistory(@AuthenticationPrincipal User user, Model model) throws Exception {
        List<EmotionHistory> histories = emotionHistoryRepository.findByUserOrderByCreatedAtDesc(user);

        ObjectMapper mapper = new ObjectMapper();
        List<EmotionHistoryView> historyViews = new ArrayList<>();

        for (EmotionHistory history : histories) {
            List<YoutubeVideo> videos = mapper.readValue(history.getVideoJson(),
                    mapper.getTypeFactory().constructCollectionType(List.class, YoutubeVideo.class));

            historyViews.add(new EmotionHistoryView(
                    history.getEmotion(),
                    history.getCreatedAt(),
                    videos
            ));
        }
        
        historyViews.sort(Comparator.comparing(EmotionHistoryView::getCreatedAt).reversed()); //날짜 순으로

        model.addAttribute("historyViews", historyViews);
        return "history";
    }

    public String getSearchQueryByEmotion(EmotionType emotionType) {
        return switch (emotionType) {
            case 기쁨 -> "들으면 기분 좋아지는 노래";
            case 슬픔 -> "슬플 때 위로해주는 노래";
            case 분노 -> "화날 때 진정되는 노래";
            case 평온 -> "마음이 편안해지는 잔잔한 음악";
            case 불안 -> "불안감에 빠졌을 때 듣는 힐링 노래";
        };
    }
}
