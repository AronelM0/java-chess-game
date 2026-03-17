package models;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private String email;
    private String password;
    private int points;
    private List<String> games = new ArrayList<>();

    public Account() {}
    public Account(String email, String password, int points) {
        this.email = email;
        this.password = password;
        this.points = points;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public List<String> getGames() { return games; }
    public void setGames(List<String> games) { this.games = games; }
}