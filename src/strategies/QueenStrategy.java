package strategies;
import models.Board;
import models.Position;
import java.util.ArrayList;
import java.util.List;

public class QueenStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Position from) {
        List<Position> moves = new ArrayList<>();
        // Reutilizez logica de la Rook și Bishop
        moves.addAll(new RookStrategy().getPossibleMoves(board, from));
        moves.addAll(new BishopStrategy().getPossibleMoves(board, from));
        return moves;
    }
}