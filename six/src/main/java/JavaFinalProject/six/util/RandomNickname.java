package JavaFinalProject.six.util;

import java.util.List;
import java.util.Random;

public class RandomNickname {
    private static final List<String> ADJECTIVES = List.of(
            "행복한", "용감한", "귀여운", "재빠른", "지혜로운", "즐거운", "수줍은", "명랑한"
    );

    private static final List<String> ANIMALS = List.of(
            "고양이", "강아지", "호랑이", "판다", "토끼", "여우", "곰", "사슴"
    );

    public static String generateRandomNickname() {
        Random random = new Random();
        String adjective = ADJECTIVES.get(random.nextInt(ADJECTIVES.size()));
        String animal = ANIMALS.get(random.nextInt(ANIMALS.size()));
        return adjective + " " + animal;
    }
}
