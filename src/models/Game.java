package models;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private String id;
    private List<Player> players = new ArrayList<>();
    private String currentPlayerColor;
    private List<Piece> board = new ArrayList<>();
    private List<Move> moves = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<Player> getPlayers() { return players; }
    public void setPlayers(List<Player> players) { this.players = players; }
    public String getCurrentPlayerColor() { return currentPlayerColor; }
    public void setCurrentPlayerColor(String currentPlayerColor) { this.currentPlayerColor = currentPlayerColor; }
    public List<Piece> getBoard() { return board; }
    public void setBoard(List<Piece> board) { this.board = board; }
    public List<Move> getMoves() { return moves; }
    public void setMoves(List<Move> moves) { this.moves = moves; }
}