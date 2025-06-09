package JavaFinalProject.six.openai;


import JavaFinalProject.six.emotion.dto.EmotionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final OpenAiClient openAiClient;

    public String generateSongRecommendationByEmotion(EmotionType emotion) {
        String prompt = getPromptByEmotion(emotion);
        return openAiClient.getChatCompletion(prompt);
    }

    private String getPromptByEmotion(EmotionType emotion) {
        return switch (emotion) {
            case 기쁨 -> "기쁠 때 들으면 좋은 K-POP 노래 5곡을 추천해줘. 제목과 가수 이름만 간단히 알려줘.";
            case 슬픔 -> "슬플 때 위로가 되는 잔잔한 K-POP 노래 5곡을 추천해줘. 제목과 가수 이름만 간단히 알려줘.";
            case 분노 -> "화가 날 때 감정을 해소할 수 있는 강렬한 K-POP 노래 5곡을 추천해줘. 제목과 가수 이름만 간단히 알려줘.";
            case 평온 -> "마음이 편안할 때 듣기 좋은 평온한 K-POP 노래 5곡을 추천해줘. 제목과 가수 이름만 간단히 알려줘.";
            case 불안 -> "불안할 때 마음을 안정시켜주는 따뜻한 K-POP 노래 5곡을 추천해줘. 제목과 가수 이름만 간단히 알려줘.";
            default -> "감정에 어울리는 K-POP 노래 5곡을 추천해줘. 제목과 가수 이름만 간단히 알려줘.";
        };
    }
}
