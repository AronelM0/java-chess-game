package models;

import java.io.Serializable;

public class GameRecord implements Serializable {
    private String result;      // "VICTORY" / "DEFEAT"
    private int whiteScore;
    private int blackScore;
    private String movesLog;    // Textul mutărilor
    private String timestamp;   // Data

    public GameRecord(String result, int whiteScore, int blackScore, String movesLog) {
        this.result = result;
        this.whiteScore = whiteScore;
        this.blackScore = blackScore;
        this.movesLog = movesLog;
        this.timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getResult() { return result; }
    public int getWhiteScore() { return whiteScore; }
    public int getBlackScore() { return blackScore; }
    public String getMovesLog() { return movesLog; }
    public String getTimestamp() { return timestamp; }
}