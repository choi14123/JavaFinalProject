package JavaFinalProject.six.song.youtube;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YoutubeService {

    @Value("${youtube.api-key}")
    private String apiKey;

    private final String BASE_URL = "https://www.googleapis.com/youtube/v3/search";

    public List<YoutubeVideo> searchByEmotion(String emotion) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("part", "snippet")
                .queryParam("maxResults", 5)
                .queryParam("q", emotion + " 노래")
                .queryParam("type", "video")
                .queryParam("key", apiKey)
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        String json = restTemplate.getForObject(url, String.class);

        List<YoutubeVideo> result = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            for (JsonNode item : root.path("items")) {
                String title = item.path("snippet").path("title").asText();
                String videoId = item.path("id").path("videoId").asText();
                String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

                result.add(new YoutubeVideo(title, videoUrl));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}