package singleton;

import models.*;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class DataManager {
    private static DataManager instance;

    private final String DATA_DIR = "data";
    private final String ACCOUNTS_PATH = DATA_DIR + "/accounts.json";
    private final String GAMES_PATH = DATA_DIR + "/games.json";

    private List<Account> accounts;
    private Map<String, Game> games;

    private DataManager() {
        ensureDataDirectoryExists();
        try {
            accounts = JsonReaderUtil.readAccounts(Paths.get(ACCOUNTS_PATH));
            games = JsonReaderUtil.readGamesAsMap(Paths.get(GAMES_PATH));
        } catch (IOException | ParseException e) {
            accounts = new ArrayList<>();
            games = new HashMap<>();
        }
    }

    public static DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    private void ensureDataDirectoryExists() {
        File directory = new File(DATA_DIR);
        if (!directory.exists()) directory.mkdirs();
    }

    // --- AUTH ---
    public boolean register(String email, String password) {
        if (getAccount(email) != null) return false;
        accounts.add(new Account(email, password, 0));
        saveAccounts();
        return true;
    }

    public boolean login(String email, String password) {
        Account acc = getAccount(email);
        return acc != null && acc.getPassword().equals(password);
    }

    public Account getAccount(String email) {
        return accounts.stream().filter(a -> a.getEmail().equals(email)).findFirst().orElse(null);
    }

    // --- STATS ---
    public int getScore(String email) {
        Account acc = getAccount(email);
        return (acc != null) ? acc.getPoints() : 0;
    }

    public int getGamesPlayed(String email) {
        Account acc = getAccount(email);
        if (acc == null || acc.getGames() == null) return 0;
        int count = 0;
        for(String id : acc.getGames()) {
            Game g = games.get(id);
            if(g != null && g.getCurrentPlayerColor() != null && g.getCurrentPlayerColor().startsWith("FINISHED")) {
                count++;
            }
        }
        return count;
    }

    public void updateStats(String email, int pointsToAdd) {
        Account acc = getAccount(email);
        if (acc != null) {
            acc.setPoints(Math.max(0, acc.getPoints() + pointsToAdd));
            saveAccounts();
        }
    }

    public void resetUserStats(String email) {
        Account acc = getAccount(email);
        if (acc != null) {
            acc.setPoints(0);
            if (acc.getGames() != null) acc.getGames().clear();
            saveAccounts();
        }
    }

    // --- ISTORIC  ---
    public List<GameRecord> getGameHistory(String email) {
        List<GameRecord> history = new ArrayList<>();
        Account acc = getAccount(email);

        if (acc != null && acc.getGames() != null) {
            for (String gameId : acc.getGames()) {
                Game g = games.get(gameId);

                if (g != null && g.getCurrentPlayerColor() != null && g.getCurrentPlayerColor().startsWith("FINISHED")) {


                    String status = g.getCurrentPlayerColor();
                    String result = "MATCH";
                    int wScore = 0;
                    int bScore = 0;

                    if (status.contains("#")) {
                        String[] parts = status.split("#");
                        if (parts.length >= 2) result = parts[1];
                        if (parts.length >= 3) wScore = Integer.parseInt(parts[2]);
                        if (parts.length >= 4) bScore = Integer.parseInt(parts[3]);
                    }


                    StringBuilder sb = new StringBuilder();
                    if(g.getMoves() != null) {
                        for(Move m : g.getMoves()) {
                            sb.append(m.getPlayerColor().equals("WHITE")?"W":"B")
                                    .append(": ").append(m.getFrom()).append("->").append(m.getTo()).append("\n");
                        }
                    }

                    history.add(new GameRecord(result, wScore, bScore, sb.toString()));
                }
            }
        }
        Collections.reverse(history);
        return history;
    }

    // --- SALVARE FINALĂ ---
    public void addGameToHistory(String email, GameRecord record, Board finalBoard) {
        deleteActiveSave(email);

        Game gameData = new Game();
        String newId = String.valueOf(System.currentTimeMillis());
        gameData.setId(newId);

        List<Player> players = new ArrayList<>();
        players.add(new Player(email, "WHITE"));
        players.add(new Player("CPU", "BLACK"));
        gameData.setPlayers(players);


        String metaData = "FINISHED#" + record.getResult() + "#" + record.getWhiteScore() + "#" + record.getBlackScore();
        gameData.setCurrentPlayerColor(metaData);

        gameData.setBoard(convertBoardToPieceList(finalBoard));
        gameData.setMoves(convertLogToMoves(record.getMovesLog()));

        games.put(newId, gameData);

        Account acc = getAccount(email);
        if(acc != null) {
            acc.getGames().add(newId);
            saveAccounts();
        }
        saveGames();
    }

    // --- SALVARE ACTIVĂ  ---
    public void saveGame(String email, Board board, boolean isWhiteTurn, String historyText) {
        Game activeGame = getActiveGameForUser(email);
        String id = (activeGame != null) ? activeGame.getId() : String.valueOf(System.currentTimeMillis());

        Game gameData = new Game();
        gameData.setId(id);

        List<Player> players = new ArrayList<>();
        players.add(new Player(email, "WHITE"));
        players.add(new Player("CPU", "BLACK"));
        gameData.setPlayers(players);

        gameData.setCurrentPlayerColor(isWhiteTurn ? "WHITE" : "BLACK");
        gameData.setBoard(convertBoardToPieceList(board));
        gameData.setMoves(convertLogToMoves(historyText));

        games.put(id, gameData);

        Account acc = getAccount(email);
        if(acc != null) {
            if (!acc.getGames().contains(id)) {
                acc.getGames().add(id);
            }
            saveAccounts();
        }
        saveGames();
    }

    public Game loadActiveGame(String email) {
        return getActiveGameForUser(email);
    }

    public void deleteActiveSave(String email) {
        Game active = getActiveGameForUser(email);
        if (active != null) {
            games.remove(active.getId());
            Account acc = getAccount(email);
            if(acc != null && acc.getGames() != null) {
                acc.getGames().remove(active.getId());
                saveAccounts();
            }
            saveGames();
        }
    }

    private Game getActiveGameForUser(String email) {
        Account acc = getAccount(email);
        if (acc != null && acc.getGames() != null) {
            for (String id : acc.getGames()) {
                Game g = games.get(id);
                // Dacă NU începe cu FINISHED, e activ
                if (g != null && g.getCurrentPlayerColor() != null && !g.getCurrentPlayerColor().startsWith("FINISHED")) {
                    return g;
                }
            }
        }
        return null;
    }

    private List<Piece> convertBoardToPieceList(Board board) {
        List<Piece> list = new ArrayList<>();
        for(int r=0; r<8; r++) {
            for(int c=0; c<8; c++) {
                Piece p = board.getPiece(new Position(r, c));
                if (p != null) list.add(p);
            }
        }
        return list;
    }

    private List<Move> convertLogToMoves(String log) {
        List<Move> moves = new ArrayList<>();
        if(log == null) return moves;
        String[] lines = log.split("\n");
        for(String line : lines) {
            try {
                if(!line.contains(":")) continue;
                String[] parts = line.split(":");
                String playerInfo = parts[0].trim().substring(0, 1);
                String[] posParts = parts[1].split("->");
                moves.add(new Move(playerInfo.equals("W") ? "WHITE" : "BLACK", posParts[0].trim(), posParts[1].trim()));
            } catch (Exception e) {}
        }
        return moves;
    }

    public List<Map.Entry<String, Integer>> getTopPlayers() {
        return accounts.stream()
                .map(a -> new AbstractMap.SimpleEntry<>(a.getEmail(), a.getPoints()))
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(10)
                .collect(Collectors.toList());
    }

    private void saveAccounts() { JsonWriterUtil.writeAccounts(accounts, ACCOUNTS_PATH); }
    private void saveGames() { JsonWriterUtil.writeGames(games, GAMES_PATH); }

    public void deleteSave(String email) { deleteActiveSave(email); }
    public GameState loadGame(String email) { return null; }
}