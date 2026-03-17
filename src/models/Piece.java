package models;
import strategies.MoveStrategy;
import java.io.Serializable;
import java.util.List;

public abstract class Piece implements Serializable {
    protected Position position;
    protected boolean isWhite;
    protected String name;
    protected MoveStrategy strategy;
    protected Colors pieceColor;

    public Piece(Colors color, Position position) {
        this.pieceColor = color;
        this.position = position;
        this.isWhite = (color == Colors.WHITE);
    }

    public Piece(Position position, boolean isWhite, String name, MoveStrategy strategy) {
        this.position = position;
        this.isWhite = isWhite;
        this.name = name;
        this.strategy = strategy;
    }

    public List<Position> getValidMoves(Board board) {
        return strategy.getPossibleMoves(board, this.position);
    }

    // --- MODIFICARE PENTRU IMAGINI (UNICODE) ---
    public String getIcon() {
        if (isWhite) {
            return switch (name) {
                case "King" -> "♔"; case "Queen" -> "♕"; case "Rook" -> "♖";
                case "Bishop" -> "♗"; case "Knight" -> "♘"; case "Pawn" -> "♙";
                default -> "?";
            };
        } else {
            return switch (name) {
                case "King" -> "♚"; case "Queen" -> "♛"; case "Rook" -> "♜";
                case "Bishop" -> "♝"; case "Knight" -> "♞"; case "Pawn" -> "♟";
                default -> "?";
            };
        }
    }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
    public boolean isWhite() { return isWhite; }
    public String getName() { return name; }

    public int getPoints() {
        return switch (name) {
            case "Queen" -> 90; case "Rook" -> 50;
            case "Bishop", "Knight" -> 30; case "Pawn" -> 10;
            default -> 0;
        };
    }
}