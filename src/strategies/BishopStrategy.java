package strategies;
import models.Board;
import models.Position;
import java.util.ArrayList;
import java.util.List;

public class BishopStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Position from) {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] dir : directions) {
            for (int i = 1; i < 8; i++) {
                int r = from.row() + dir[0] * i;
                int c = from.col() + dir[1] * i;
                if (!board.isValidPosition(r, c)) break;

                var target = board.getPiece(new Position(r, c));
                if (target == null) {
                    moves.add(new Position(r, c));
                } else {
                    if (target.isWhite() != board.getPiece(from).isWhite()) {
                        moves.add(new Position(r, c));
                    }
                    break;
                }
            }
        }
        return moves;
    }
}