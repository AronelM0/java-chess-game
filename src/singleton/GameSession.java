package singleton;

import models.Board;
import models.GameRecord;
import models.GameState;
import models.Game;
import models.Piece;
import models.Move;
import models.Position;
import java.util.List;

public class GameSession {
    private static GameSession instance;
    private String userEmail;
    private int currentGameScore = 0;
    private boolean userPlaysWhite = true;
    private GameState loadedState = null;
    private String loadedHistoryLog = "";

    private GameSession() {}

    public static GameSession getInstance() {
        if (instance == null) instance = new GameSession();
        return instance;
    }

    public void saveFinishedGame(String result, int wScore, int bScore, String historyLog, Board finalBoard) {
        GameRecord record = new GameRecord(result, wScore, bScore, historyLog);
        DataManager.getInstance().addGameToHistory(userEmail, record, finalBoard);
    }

    public List<GameRecord> getMyHistory() {
        return DataManager.getInstance().getGameHistory(userEmail);
    }

    public boolean login(String email, String password) {
        if (DataManager.getInstance().login(email, password)) {
            this.userEmail = email;
            return true;
        }
        return false;
    }

    public boolean register(String email, String password) {
        return DataManager.getInstance().register(email, password);
    }

    public void startNewGame(boolean playAsWhite) {
        currentGameScore = 0;
        loadedState = null;
        loadedHistoryLog = "";
        this.userPlaysWhite = playAsWhite;
        DataManager.getInstance().deleteActiveSave(userEmail);

        Board freshBoard = new Board();
        DataManager.getInstance().saveGame(userEmail, freshBoard, true, "");
    }

    public void resetAccountStats() {
        DataManager.getInstance().resetUserStats(userEmail);
    }

    public boolean loadSavedGame() {
        Game savedGame = DataManager.getInstance().loadActiveGame(userEmail);

        if (savedGame != null) {
            Board board = new Board();
            for(int r=0; r<8; r++) for(int c=0; c<8; c++) board.setPiece(new Position(r,c), null);

            if(savedGame.getBoard() != null) {
                for(Piece p : savedGame.getBoard()) {
                    board.setPiece(p.getPosition(), p);
                }
            }

            boolean isWhiteTurn = "WHITE".equals(savedGame.getCurrentPlayerColor());

            StringBuilder sb = new StringBuilder();
            if(savedGame.getMoves() != null) {
                for(Move m : savedGame.getMoves()) {
                    String prefix = m.getPlayerColor().equals("WHITE") ? "W" : "B";
                    sb.append(prefix).append(" Piece : ").append(m.getFrom()).append(" -> ").append(m.getTo()).append("\n");
                }
            }
            this.loadedHistoryLog = sb.toString();

            this.loadedState = new GameState(board, isWhiteTurn, 0);
            this.currentGameScore = 0;
            return true;
        }
        return false;
    }

    public void saveCurrentGame(GameState state, String historyLog) {
        DataManager.getInstance().saveGame(userEmail, state.board, state.isPlayerTurn, historyLog);
    }

    public GameState getLoadedState() { return loadedState; }
    public String getLoadedHistoryLog() { return loadedHistoryLog; }

    public void addGamePoints(int points) { currentGameScore += points; }

    public void endGame(boolean victory, boolean byCheckmate) {
        int bonus = victory ? (byCheckmate ? 300 : 150) : (byCheckmate ? -300 : -150);
        DataManager.getInstance().updateStats(userEmail, currentGameScore + bonus);
    }

    public int getTotalAccountScore() { return DataManager.getInstance().getScore(userEmail); }
    public int getGamesPlayed() { return DataManager.getInstance().getGamesPlayed(userEmail); }
    public int getCurrentGameScore() { return currentGameScore; }
    public String getUserEmail() { return userEmail; }
    public boolean isUserWhite() { return userPlaysWhite; }
}