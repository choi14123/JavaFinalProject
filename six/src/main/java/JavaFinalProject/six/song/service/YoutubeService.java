package JavaFinalProject.six.song.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import JavaFinalProject.six.song.YoutubeVideo;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class YoutubeService {

    @Value("${youtube.api-key}")
    private String apiKey;

    private final String SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";
    private final String VIDEO_URL = "https://www.googleapis.com/youtube/v3/videos";

    public Map<String, List<YoutubeVideo>> searchByEmotionSeparated(String emotionQuery) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. Search API 호출 (videoId 얻기)
        String searchUrl = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                .queryParam("part", "snippet")
                .queryParam("maxResults", 10)
                .queryParam("q", emotionQuery)
                .queryParam("type", "video")
                .queryParam("key", apiKey)
                .toUriString();

        String searchJson = restTemplate.getForObject(searchUrl, String.class);
        List<String> videoIds = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(searchJson);
            for (JsonNode item : root.path("items")) {
                String videoId = item.path("id").path("videoId").asText();
                if (!videoId.isEmpty()) {
                    videoIds.add(videoId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Videos API 호출 (분류)
        String videoDetailUrl = UriComponentsBuilder.fromHttpUrl(VIDEO_URL)
                .queryParam("part", "snippet")
                .queryParam("id", String.join(",", videoIds))
                .queryParam("key", apiKey)
                .toUriString();

        String detailJson = restTemplate.getForObject(videoDetailUrl, String.class);
        List<YoutubeVideo> singles = new ArrayList<>();
        List<YoutubeVideo> playlists = new ArrayList<>();

        try {
            JsonNode root = mapper.readTree(detailJson);
            for (JsonNode item : root.path("items")) {
                String categoryId = item.path("snippet").path("categoryId").asText();
                String title = item.path("snippet").path("title").asText();
                boolean isPlaylistLike = title.contains("모음") || title.toLowerCase().contains("playlist") ||
                                          title.contains("연속") || title.toLowerCase().contains("nonstop");

                if ("10".equals(categoryId)) {
                    String videoId = item.path("id").asText();
                    String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                    String thumbnailUrl = item.path("snippet").path("thumbnails").path("medium").path("url").asText();

                    YoutubeVideo video = new YoutubeVideo(title, videoUrl, thumbnailUrl);
                    if (isPlaylistLike) {
                        playlists.add(video);
                    } else {
                        singles.add(video);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, List<YoutubeVideo>> result = new HashMap<>();
        result.put("singles", singles);
        result.put("playlists", playlists);
        return result;
    }
}
