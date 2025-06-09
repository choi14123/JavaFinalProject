package JavaFinalProject.six.emotion;

import JavaFinalProject.six.emotion.dto.EmotionSaveRequest;
import JavaFinalProject.six.security.jwt.JwtTokenProvider;
import JavaFinalProject.six.user.service.UserService;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import JavaFinalProject.six.emotion.dto.EmotionHistoryView;
import JavaFinalProject.six.emotion.dto.EmotionType;
import JavaFinalProject.six.song.YoutubeVideo;
import JavaFinalProject.six.song.service.YoutubeService;
import JavaFinalProject.six.user.User;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/emotion")
@RequiredArgsConstructor
public class EmotionController {

	private final YoutubeService youtubeService;
    private final EmotionHistoryService emotionHistoryService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @GetMapping("/select")
    public String showEmotionPage(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, Model model) {
        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmail(token);
        User user = userService.findByEmail(email);

        System.out.println("로그인 사용자: " + user.getEmail());

        model.addAttribute("emotions", EmotionType.values());
        return "select";
    }

    @GetMapping("/result")
    public String showEmotionResult(
            @RequestParam("emotion") String emotionParam,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            Model model) throws Exception {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmail(token);
        User user = userService.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        EmotionType emotionType = EmotionType.valueOf(URLDecoder.decode(emotionParam, StandardCharsets.UTF_8));
        List<YoutubeVideo> videos = youtubeService.searchByEmotion(getSearchQueryByEmotion(emotionType));

        String videoJson = new ObjectMapper().writeValueAsString(videos);

        model.addAttribute("emotion", emotionType);
        model.addAttribute("videos", videos);
        model.addAttribute("videoJson", videoJson);

        return "result";
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveEmotionResult(
            @RequestBody EmotionSaveRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String token = authorizationHeader.replace("Bearer ", "");

        // JWT에서 이메일 추출
        String email;
        try {
            email = jwtTokenProvider.getEmail(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        // 사용자 조회
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

        // 감정 Enum 변환
        EmotionType emotionType;
        try {
            emotionType = EmotionType.valueOf(URLDecoder.decode(request.getEmotion(), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("유효하지 않은 감정 값입니다.");
        }

        // 저장
        EmotionHistory history = EmotionHistory.builder()
                .user(user)
                .emotion(emotionType)
                .createdAt(LocalDateTime.now())
                .videoJson(request.getVideoJson())
                .build();

        emotionHistoryService.save(history);

        return ResponseEntity.ok("추천 결과가 성공적으로 저장되었습니다.");
    }


    @GetMapping("/history")
    public String showEmotionHistory(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            Model model) throws Exception {

        // 1. JWT 헤더 검증
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        String token = authorizationHeader.substring(7); // "Bearer " 제거
        String email;

        try {
            email = jwtTokenProvider.getEmail(token);
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // 2. 사용자 조회
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 3. 감정 기록 조회
        List<EmotionHistory> histories = emotionHistoryService.findByUserOrderByCreatedAtDesc(user);

        ObjectMapper mapper = new ObjectMapper();
        List<EmotionHistoryView> historyViews = new ArrayList<>();

        for (EmotionHistory history : histories) {
            List<YoutubeVideo> videos = mapper.readValue(
                    history.getVideoJson(),
                    mapper.getTypeFactory().constructCollectionType(List.class, YoutubeVideo.class)
            );

            historyViews.add(new EmotionHistoryView(
                    history.getEmotion(),
                    history.getCreatedAt(),
                    videos
            ));
        }

        model.addAttribute("historyViews", historyViews);
        return "history"; // Thymeleaf 템플릿: templates/history.html
    }

    public String getSearchQueryByEmotion(EmotionType emotionType) {
        // 감정별 걸맞는 검색 문구로 변환
        return switch (emotionType) {
            case 기쁨 -> "들으면 기분 좋아지는 노래";
            case 슬픔 -> "슬플 때 위로해주는 노래";
            case 분노 -> "화날 때 진정되는 노래";
            case 평온 -> "마음이 편안해지는 잔잔한 음악";
            case 불안 -> "불안감에 빠졌을 때 듣는 힐링 노래";
        };
    }
}