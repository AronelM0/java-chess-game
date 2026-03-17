package strategies;
import models.Board;
import models.Position;
import java.io.Serializable;
import java.util.List;

public interface MoveStrategy extends Serializable {
    List<Position> getPossibleMoves(Board board, Position from);
}