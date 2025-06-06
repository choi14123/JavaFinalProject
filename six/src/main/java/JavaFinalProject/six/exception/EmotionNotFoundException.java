package JavaFinalProject.six.exception;

import JavaFinalProject.six.emotion.Emotion;

public class EmotionNotFoundException extends RuntimeException {
    public EmotionNotFoundException(String emotion) {
        super("감정 '" + emotion + "' 을(를) 찾을 수 없습니다.");
    }
}