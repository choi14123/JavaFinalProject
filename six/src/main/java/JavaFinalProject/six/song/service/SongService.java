package JavaFinalProject.six.song.service;

import JavaFinalProject.six.song.Song;
import JavaFinalProject.six.song.dto.request.SongRequest;
import JavaFinalProject.six.song.dto.response.SongResponse;
import JavaFinalProject.six.song.repository.SongRepository;
import JavaFinalProject.six.emotion.Emotion;
import JavaFinalProject.six.emotion.EmotionRepository;
import JavaFinalProject.six.exception.EmotionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final EmotionRepository emotionRepository;

    public SongResponse save(SongRequest dto) {
        Emotion emotion = emotionRepository.findByName(dto.getEmotion());
        if (emotion == null) throw new EmotionNotFoundException(dto.getEmotion());

        Song song = Song.builder()
                .title(dto.getTitle())
                .artist(dto.getArtist())
                .emotion(emotion)
                .build();

        songRepository.save(song);

        return new SongResponse(song.getId(), song.getTitle(), song.getArtist(), emotion.getName());
    }

    public List<SongResponse> findByEmotion(String name) {
        Emotion emotion = emotionRepository.findByName(name);
        if (emotion == null) throw new EmotionNotFoundException(name);

        return songRepository.findByEmotion(emotion).stream()
                .map(song -> new SongResponse(song.getId(), song.getTitle(), song.getArtist(), emotion.getName()))
                .collect(Collectors.toList());
    }
}