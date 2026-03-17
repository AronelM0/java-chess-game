package singleton;

import models.*;
import pieces.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JsonReaderUtil {
    private JsonReaderUtil() {}

    public static List<Account> readAccounts(Path path) throws IOException, ParseException {
        if (path == null || !Files.exists(path)) return new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            Object root = parser.parse(reader);
            JSONArray arr = asArray(root);
            List<Account> result = new ArrayList<>();
            if (arr == null) return result;

            for (Object item : arr) {
                JSONObject obj = asObject(item);
                if (obj == null) continue;
                Account acc = new Account();
                acc.setEmail(asString(obj.get("email")));
                acc.setPassword(asString(obj.get("password")));
                acc.setPoints(asInt(obj.get("points"), 0));

                List<String> gameIds = new ArrayList<>();
                JSONArray games = asArray(obj.get("games"));
                if (games != null) {
                    for (Object gid : games) {
                        gameIds.add(String.valueOf(gid));
                    }
                }
                acc.setGames(gameIds);
                result.add(acc);
            }
            return result;
        }
    }

    public static Map<String, Game> readGamesAsMap(Path path) throws IOException, ParseException {
        Map<String, Game> map = new HashMap<>();
        if (path == null || !Files.exists(path)) return map;
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            Object root = parser.parse(reader);
            JSONArray arr = asArray(root);
            if (arr == null) return map;

            for (Object item : arr) {
                JSONObject obj = asObject(item);
                if (obj == null) continue;

                String id = String.valueOf(obj.get("id"));

                Game g = new Game();
                g.setId(id);


                JSONArray playersArr = asArray(obj.get("players"));
                if (playersArr != null) {
                    List<Player> players = new ArrayList<>();
                    for (Object pItem : playersArr) {
                        JSONObject pObj = asObject(pItem);
                        if (pObj == null) continue;
                        players.add(new Player(asString(pObj.get("email")), asString(pObj.get("color"))));
                    }
                    g.setPlayers(players);
                }
                g.setCurrentPlayerColor(asString(obj.get("currentPlayerColor")));

                JSONArray boardArr = asArray(obj.get("board"));
                if (boardArr != null) {
                    List<Piece> board = new ArrayList<>();
                    for (Object bItem : boardArr) {
                        JSONObject bObj = asObject(bItem);
                        if (bObj == null) continue;
                        String type = asString(bObj.get("type"));
                        String colorStr = asString(bObj.get("color"));
                        String posStr = asString(bObj.get("position"));
                        Colors col = "WHITE".equals(colorStr) ? Colors.WHITE : Colors.BLACK;
                        Position pos = new Position(posStr);
                        Piece p = null;
                        if(type != null && !type.isEmpty()) {
                            switch(type.charAt(0)) {
                                case 'K': p = new King(col, pos); break;
                                case 'Q': p = new Queen(col, pos); break;
                                case 'R': p = new Rook(col, pos); break;
                                case 'B': p = new Bishop(col, pos); break;
                                case 'N': p = new Knight(col, pos); break;
                                case 'P': p = new Pawn(col, pos); break;
                            }
                        }
                        if(p != null) board.add(p);
                    }
                    g.setBoard(board);
                }

                JSONArray movesArr = asArray(obj.get("moves"));
                if (movesArr != null) {
                    List<Move> moves = new ArrayList<>();
                    for (Object mItem : movesArr) {
                        JSONObject mObj = asObject(mItem);
                        if (mObj == null) continue;
                        moves.add(new Move(asString(mObj.get("playerColor")), asString(mObj.get("from")), asString(mObj.get("to"))));
                    }
                    g.setMoves(moves);
                }
                map.put(id, g);
            }
        }
        return map;
    }

    private static JSONArray asArray(Object o) { return (o instanceof JSONArray) ? (JSONArray) o : null; }
    private static JSONObject asObject(Object o) { return (o instanceof JSONObject) ? (JSONObject) o : null; }
    private static String asString(Object o) { return o == null ? null : String.valueOf(o); }
    private static int asInt(Object o, int def) {
        if (o instanceof Number) return ((Number) o).intValue();
        try { return o != null ? Integer.parseInt(String.valueOf(o)) : def; } catch (Exception e) { return def; }
    }
}