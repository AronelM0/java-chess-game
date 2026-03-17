package models;

public class Move {
    private String playerColor;
    private String from;
    private String to;

    public Move(String playerColor, String from, String to) {
        this.playerColor = playerColor;
        this.from = from;
        this.to = to;
    }

    public String getPlayerColor() { return playerColor; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
}