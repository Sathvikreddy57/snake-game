import java.io.*;
import java.nio.file.*;

public class HighScoreManager {
    private static final String HIGH_SCORE_FILE = "highscore.txt";
    
    public static int getHighScore() {
        try {
            if (Files.exists(Paths.get(HIGH_SCORE_FILE))) {
                String content = new String(Files.readAllBytes(Paths.get(HIGH_SCORE_FILE)));
                return Integer.parseInt(content.trim());
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setHighScore(int score) {
        try {
            Files.write(Paths.get(HIGH_SCORE_FILE), String.valueOf(score).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
