package observer;
import models.Piece;

public interface GameObserver {
    void onBoardUpdate();
    void onPieceCaptured(Piece piece);
}