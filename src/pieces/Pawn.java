package pieces;

import models.*;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(Colors color, Position position) {
        super(color, position);
        this.name = "Pawn";
    }

    @Override
    public List<Position> getValidMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int direction = isWhite() ? -1 : 1;
        int r = position.row();
        int c = position.col();

        // 1. Mutare înainte 1 pătrat
        Position forwardOne = new Position(r + direction, c);
        if (!board.isOutOfBounds(forwardOne) && board.getPiece(forwardOne) == null) {
            moves.add(forwardOne);

            // 2. Mutare înainte 2 pătrate (doar de la start)
            boolean isStart = (isWhite() && r == 6) || (!isWhite() && r == 1);
            Position forwardTwo = new Position(r + (direction * 2), c);
            if (isStart && !board.isOutOfBounds(forwardTwo) && board.getPiece(forwardTwo) == null) {
                moves.add(forwardTwo);
            }
        }

        // 3. Captură stânga
        Position captureLeft = new Position(r + direction, c - 1);
        if (!board.isOutOfBounds(captureLeft)) {
            Piece p = board.getPiece(captureLeft);
            if (p != null && p.isWhite() != this.isWhite()) {
                moves.add(captureLeft);
            }
        }

        // 4. Captură dreapta
        Position captureRight = new Position(r + direction, c + 1);
        if (!board.isOutOfBounds(captureRight)) {
            Piece p = board.getPiece(captureRight);
            if (p != null && p.isWhite() != this.isWhite()) {
                moves.add(captureRight);
            }
        }

        return moves;
    }
}