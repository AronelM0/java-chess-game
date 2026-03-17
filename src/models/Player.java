package models;

public class Player {
    private String email;
    private String color; // "WHITE" or "BLACK"

    public Player(String email, String color) {
        this.email = email;
        this.color = color;
    }

    public String getEmail() { return email; }
    public String getColor() { return color; }
}