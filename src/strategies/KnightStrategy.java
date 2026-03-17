package strategies;
import models.Board;
import models.Position;
import java.util.ArrayList;
import java.util.List;

public class KnightStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Position from) {
        List<Position> moves = new ArrayList<>();
        int[][] offsets = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}};

        for (int[] off : offsets) {
            int r = from.row() + off[0];
            int c = from.col() + off[1];
            if (board.isValidPosition(r, c)) {
                var target = board.getPiece(new Position(r, c));
                if (target == null || target.isWhite() != board.getPiece(from).isWhite()) {
                    moves.add(new Position(r, c));
                }
            }
        }
        return moves;
    }
}