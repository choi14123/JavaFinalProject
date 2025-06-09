package JavaFinalProject.six.emotion;

import JavaFinalProject.six.emotion.repository.EmotionRepository;
import JavaFinalProject.six.openai.OpenAiService;
import JavaFinalProject.six.security.jwt.JwtTokenProvider;
import JavaFinalProject.six.song.Song;
import JavaFinalProject.six.song.SongRepository;
import JavaFinalProject.six.user.repository.UserRepository;
import JavaFinalProject.six.user.service.UserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import JavaFinalProject.six.emotion.dto.EmotionHistoryView;
import JavaFinalProject.six.emotion.dto.EmotionType;
import JavaFinalProject.six.emotion.repository.EmotionHistoryRepository;
import JavaFinalProject.six.song.YoutubeVideo;
import JavaFinalProject.six.user.User;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/emotion")
@RequiredArgsConstructor
public class EmotionController {

    private final OpenAiService openAiService;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmotionHistoryRepository emotionHistoryRepository;
    private final UserRepository userRepository;
    private final EmotionRepository emotionRepository;
    private final SongRepository songRepository;
    private final UserService userService;

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

        String str = openAiService.generateSongRecommendationByEmotion(emotionType);
        System.out.println("str : " + str);

        List<String> songList = Arrays.stream(str.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        String songText = String.join("||", songList); // 저장용 문자열

        model.addAttribute("emotion", emotionType);
        model.addAttribute("songList", songList);
        model.addAttribute("videoJson", songText); // 기존 필드 이름 그대로 사용

        return "result";
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveEmotionResult(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("emotion") EmotionType emotionType,
            @RequestParam("videoJson") String videoJson) {

        try {
            // 1. 사용자 인증
            String token = authorizationHeader.replace("Bearer ", "");
            String email = jwtTokenProvider.getEmail(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

            // 2. Emotion 객체 조회
            Emotion emotion = emotionRepository.findByEmotionType(emotionType)
                    .orElseThrow(() -> new IllegalArgumentException("해당 감정이 존재하지 않습니다: " + emotionType));

            // 3. EmotionHistory 생성
            EmotionHistory history = EmotionHistory.builder()
                    .user(user)
                    .emotion(emotionType)
                    .createdAt(LocalDateTime.now())
                    .build();

            // 4. 추천된 노래 리스트 생성
            List<Song> songList = Arrays.stream(videoJson.split("\\|\\|"))
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(line -> {
                        try {
                            String[] parts = line.split("\\. ", 2);
                            String[] artistTitle = parts[1].split(" - ", 2);
                            String title = artistTitle[0].trim();
                            String artist = artistTitle[1].trim();
                            System.out.println(artistTitle);
                            System.out.println(artist);
                            System.out.println(title);
                            return Song.builder()
                                    .title(title)
                                    .artist(artist)
                                    .user(user)
                                    .emotion(emotion)
                                    .emotionHistory(history)
                                    .build();
                        } catch (Exception e) {
                            System.err.println("파싱 실패: " + line + " → " + e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();

            // 5. 연관관계 주입
            history.setSongs(songList);

            // 6. 저장
            emotionHistoryRepository.save(history); // Cascade로 songList도 자동 저장

            return ResponseEntity.ok("곡 추천 결과가 성공적으로 저장되었습니다.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("저장 중 오류 발생: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    public String showEmotionHistory(@RequestHeader("Authorization") String authorizationHeader,
                                     Model model) throws Exception {
        // 1. JWT에서 사용자 이메일 추출
        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmail(token);

        // 2. 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        // 3. 해당 사용자의 감정 기록 조회
        List<EmotionHistory> histories = emotionHistoryRepository.findByUserOrderByCreatedAtDesc(user);

        // 4. EmotionHistory → EmotionHistoryView로 변환
        List<EmotionHistoryView> historyViews = histories.stream()
                .map(history -> {
                    List<Song> songs = history.getSongs();  // EmotionHistory → Song 리스트
                    return new EmotionHistoryView(
                            history.getEmotion(),
                            history.getCreatedAt(),
                            songs
                    );
                })
                .sorted(Comparator.comparing(EmotionHistoryView::getCreatedAt).reversed())
                .toList();


        // 5. Thymeleaf 전달
        model.addAttribute("historyViews", historyViews);
        return "history";
    }

}
