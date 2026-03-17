package strategies;
import models.Board;
import models.Position;
import java.util.ArrayList;
import java.util.List;

public class PawnStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Position from) {
        List<Position> moves = new ArrayList<>();
        boolean isWhite = board.getPiece(from).isWhite();
        int direction = isWhite ? -1 : 1; // Alb merge în sus (index scade), Negru în jos

        // --- 1. Mutare normală (1 pas) ---
        int r1 = from.row() + direction;
        int c = from.col();

        if (board.isValidPosition(r1, c) && board.getPiece(new Position(r1, c)) == null) {
            moves.add(new Position(r1, c));

            // --- 2. Mutare dublă (2 pași) - DOAR dacă primul pas a fost liber ---
            boolean isStartRow = (isWhite && from.row() == 6) || (!isWhite && from.row() == 1);
            int r2 = from.row() + (direction * 2);

            if (isStartRow && board.isValidPosition(r2, c) && board.getPiece(new Position(r2, c)) == null) {
                moves.add(new Position(r2, c));
            }
        }

        // --- 3. Captură pe diagonală ---
        int[] captureCols = {c - 1, c + 1};
        for (int capCol : captureCols) {
            if (board.isValidPosition(r1, capCol)) {
                var target = board.getPiece(new Position(r1, capCol));
                if (target != null && target.isWhite() != isWhite) {
                    moves.add(new Position(r1, capCol));
                }
            }
        }
        return moves;
    }
}