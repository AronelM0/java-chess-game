package singleton;
import models.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonWriterUtil {
    @SuppressWarnings("unchecked")
    public static void writeAccounts(List<Account> accounts, String path) {
        JSONArray list = new JSONArray();
        for (Account acc : accounts) {
            JSONObject obj = new JSONObject();
            obj.put("email", acc.getEmail());
            obj.put("password", acc.getPassword());
            obj.put("points", acc.getPoints());
            JSONArray games = new JSONArray();
            if (acc.getGames() != null) games.addAll(acc.getGames());
            obj.put("games", games);
            list.add(obj);
        }
        try (FileWriter file = new FileWriter(path)) {
            file.write(list.toJSONString());
            file.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    public static void writeGames(Map<String, Game> gamesMap, String path) {
        JSONArray list = new JSONArray();
        for (Game g : gamesMap.values()) {
            JSONObject obj = new JSONObject();
            obj.put("id", g.getId());
            obj.put("currentPlayerColor", g.getCurrentPlayerColor());

            JSONArray playersArr = new JSONArray();
            for (Player p : g.getPlayers()) {
                JSONObject pObj = new JSONObject();
                pObj.put("email", p.getEmail());
                pObj.put("color", p.getColor());
                playersArr.add(pObj);
            }
            obj.put("players", playersArr);

            JSONArray boardArr = new JSONArray();
            if (g.getBoard() != null) {
                for (Piece p : g.getBoard()) {
                    JSONObject bObj = new JSONObject();
                    String type = p.getName().substring(0, 1);
                    if (p.getName().equals("Knight")) type = "N";
                    bObj.put("type", type);
                    bObj.put("color", p.isWhite() ? "WHITE" : "BLACK");
                    bObj.put("position", p.getPosition().toString());
                    boardArr.add(bObj);
                }
            }
            obj.put("board", boardArr);

            JSONArray movesArr = new JSONArray();
            if (g.getMoves() != null) {
                for (Move m : g.getMoves()) {
                    JSONObject mObj = new JSONObject();
                    mObj.put("playerColor", m.getPlayerColor());
                    mObj.put("from", m.getFrom());
                    mObj.put("to", m.getTo());
                    movesArr.add(mObj);
                }
            }
            obj.put("moves", movesArr);
            list.add(obj);
        }
        try (FileWriter file = new FileWriter(path)) {
            file.write(list.toJSONString());
            file.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}