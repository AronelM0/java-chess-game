package pieces;

import models.*;
import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(Colors color, Position position) {
        super(color, position);
        this.name = "Queen";
    }

    @Override
    public List<Position> getValidMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        // Combinație Rook + Bishop
        int[][] directions = {
                {-1,0}, {1,0}, {0,-1}, {0,1}, // Linii
                {-1,-1}, {-1,1}, {1,-1}, {1,1} // Diagonale
        };

        for (int[] d : directions) {
            for (int i = 1; i < 8; i++) {
                Position target = new Position(position.row() + d[0] * i, position.col() + d[1] * i);
                if (board.isOutOfBounds(target)) break;

                Piece p = board.getPiece(target);
                if (p == null) {
                    moves.add(target);
                } else {
                    if (p.isWhite() != this.isWhite()) {
                        moves.add(target);
                    }
                    break;
                }
            }
        }
        return moves;
    }
}