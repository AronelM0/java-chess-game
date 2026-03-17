package pieces;

import models.*;
import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(Colors color, Position position) {
        super(color, position);
        this.name = "Knight";
    }

    @Override
    public List<Position> getValidMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int[][] offsets = {
                {-2,-1}, {-2,1}, {-1,-2}, {-1,2},
                {1,-2}, {1,2}, {2,-1}, {2,1}
        };

        for (int[] o : offsets) {
            Position target = new Position(position.row() + o[0], position.col() + o[1]);
            if (!board.isOutOfBounds(target)) {
                Piece p = board.getPiece(target);
                if (p == null || p.isWhite() != this.isWhite()) {
                    moves.add(target);
                }
            }
        }
        return moves;
    }
}