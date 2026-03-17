package strategies;
import models.Board;
import models.Position;
import java.util.ArrayList;
import java.util.List;

public class KingStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Position from) {
        List<Position> moves = new ArrayList<>();
        // Toate cele 8 pătrate din jur
        for (int r = -1; r <= 1; r++) {
            for (int c = -1; c <= 1; c++) {
                if (r == 0 && c == 0) continue;
                int nextRow = from.row() + r;
                int nextCol = from.col() + c;

                if (board.isValidPosition(nextRow, nextCol)) {
                    var target = board.getPiece(new Position(nextRow, nextCol));
                    if (target == null || target.isWhite() != board.getPiece(from).isWhite()) {
                        moves.add(new Position(nextRow, nextCol));
                    }
                }
            }
        }
        return moves;
    }
}