package models;

import java.io.Serializable;

public class Position implements Serializable {
    private int row;
    private int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }


    public Position(String pos) {
        if (pos != null && pos.length() >= 2) {
            char colChar = pos.toUpperCase().charAt(0);
            char rowChar = pos.charAt(1);
            this.col = colChar - 'A';
            this.row = 8 - (rowChar - '0');
        }
    }

    public int row() { return row; }
    public int col() { return col; }


    @Override
    public String toString() {
        char colChar = (char) ('A' + col);
        int rowNum = 8 - row;
        return "" + colChar + rowNum;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}