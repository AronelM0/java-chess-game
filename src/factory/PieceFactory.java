package factory;

import models.Piece;
import models.Position;
import models.Colors;
import pieces.*;

public class PieceFactory {

    public static Piece createPiece(String type, Position pos, boolean isWhite) {
        Colors color = isWhite ? Colors.WHITE : Colors.BLACK;

        return switch (type) {
            case "Pawn" -> new Pawn(color, pos);
            case "Rook" -> new Rook(color, pos);
            case "Knight" -> new Knight(color, pos);
            case "Bishop" -> new Bishop(color, pos);
            case "Queen" -> new Queen(color, pos);
            case "King" -> new King(color, pos);
            default -> null;
        };
    }
}