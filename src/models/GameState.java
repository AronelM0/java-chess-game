package models;
import java.io.Serializable;

public class GameState implements Serializable {
    public Board board;
    public boolean isPlayerTurn;
    public int currentScore;

    public GameState(Board board, boolean isPlayerTurn, int currentScore) {
        this.board = board;
        this.isPlayerTurn = isPlayerTurn;
        this.currentScore = currentScore;
    }
}