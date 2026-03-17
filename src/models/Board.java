package models;

import pieces.*;
import java.io.Serializable;

public class Board implements Serializable {
    private Piece[][] grid;

    public Board() {
        grid = new Piece[8][8];
        resetBoard();
    }

    public void resetBoard() {
        // Pioni
        for (int c = 0; c < 8; c++) {
            grid[1][c] = new Pawn(Colors.BLACK, new Position(1, c));
            grid[6][c] = new Pawn(Colors.WHITE, new Position(6, c));
        }

        // Piese Negre
        grid[0][0] = new Rook(Colors.BLACK, new Position(0, 0));
        grid[0][7] = new Rook(Colors.BLACK, new Position(0, 7));
        grid[0][1] = new Knight(Colors.BLACK, new Position(0, 1));
        grid[0][6] = new Knight(Colors.BLACK, new Position(0, 6));
        grid[0][2] = new Bishop(Colors.BLACK, new Position(0, 2));
        grid[0][5] = new Bishop(Colors.BLACK, new Position(0, 5));
        grid[0][3] = new Queen(Colors.BLACK, new Position(0, 3));
        grid[0][4] = new King(Colors.BLACK, new Position(0, 4));

        // Piese Albe
        grid[7][0] = new Rook(Colors.WHITE, new Position(7, 0));
        grid[7][7] = new Rook(Colors.WHITE, new Position(7, 7));
        grid[7][1] = new Knight(Colors.WHITE, new Position(7, 1));
        grid[7][6] = new Knight(Colors.WHITE, new Position(7, 6));
        grid[7][2] = new Bishop(Colors.WHITE, new Position(7, 2));
        grid[7][5] = new Bishop(Colors.WHITE, new Position(7, 5));
        grid[7][3] = new Queen(Colors.WHITE, new Position(7, 3));
        grid[7][4] = new King(Colors.WHITE, new Position(7, 4));
    }

    public Piece getPiece(Position pos) {
        if (isOutOfBounds(pos)) return null;
        return grid[pos.row()][pos.col()];
    }

    public void setPiece(Position pos, Piece piece) {
        if (isOutOfBounds(pos)) return;
        grid[pos.row()][pos.col()] = piece;
        if (piece != null) {
            piece.setPosition(pos);
        }
    }

    public void movePiece(Position start, Position end) {
        Piece p = getPiece(start);
        if (p != null) {
            setPiece(end, p);
            setPiece(start, null);
        }
    }

    public boolean isOutOfBounds(Position pos) {
        return pos.row() < 0 || pos.row() > 7 || pos.col() < 0 || pos.col() > 7;
    }


    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }


    public String getAlgebraicNotation(Position pos) {
        char col = (char) ('A' + pos.col());
        int row = 8 - pos.row();
        return "" + col + row;
    }

    // Găsirea regelui
    public Position findKing(boolean isWhite) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = grid[r][c];
                if (p != null && p instanceof King && p.isWhite() == isWhite) {
                    return p.getPosition();
                }
            }
        }
        return null;
    }

    // Verifică dacă un pătrat e atacat
    public boolean isSquareAttacked(Position pos, boolean byWhite) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = grid[r][c];
                if (p != null && p.isWhite() == byWhite) {
                    if (p.getValidMoves(this).contains(pos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void reattachObservers() {}
    public void addObserver(observer.GameObserver o) {}
}