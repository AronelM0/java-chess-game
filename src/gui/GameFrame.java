package gui;

import factory.PieceFactory;
import models.Board;
import models.GameState;
import models.Piece;
import models.Position;
import observer.GameObserver;
import singleton.GameSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameFrame extends JFrame implements GameObserver {
    private Board board;
    private JButton[][] squares = new JButton[8][8];
    private Position selectedPos = null;

    private boolean isPlayerTurn;
    private boolean userPlaysWhite;
    private boolean isGameOver = false;
    private int whiteScore = 0;
    private int blackScore = 0;

    private JLabel statusLabel;
    private JTextArea historyArea;
    private JLabel scoreLabelWhite;
    private JLabel scoreLabelBlack;
    private JLabel scoreLabel;

    private JPanel whiteCapturedPanel;
    private JPanel blackCapturedPanel;
    private JButton saveBtn;
    private JButton resignBtn;

    private Font pieceFont;
    private final Color LIGHT_SQUARE = new Color(223, 230, 233);
    private final Color DARK_SQUARE = new Color(84, 109, 126);
    private final Color SELECTED_COLOR = new Color(255, 234, 167);
    private final Color MOVE_COLOR = new Color(129, 236, 236);
    private final Color SIDEBAR_BG = new Color(45, 52, 54);
    private final Color BOARD_BG = new Color(30, 30, 30);

    public GameFrame() {
        GameState loaded = GameSession.getInstance().getLoadedState();

        if (loaded != null) {
            this.board = loaded.board;
            this.isPlayerTurn = loaded.isPlayerTurn;
            this.userPlaysWhite = GameSession.getInstance().isUserWhite();
            this.board.reattachObservers();
        } else {
            this.board = new Board();
            this.userPlaysWhite = GameSession.getInstance().isUserWhite();
            this.isPlayerTurn = this.userPlaysWhite;
        }
        this.board.addObserver(this);

        setTitle("ChessMate - Play");
        setAppIcon();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. CALCUL DIMENSIUNI
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = ge.getMaximumWindowBounds();
        int availableHeight = bounds.height - 80;
        int boardSize = availableHeight;
        int fontSize = (int)((boardSize / 8.0) * 0.75);
        pieceFont = new Font("Serif", Font.BOLD, fontSize);

        // 2. TOP PANEL
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(45, 52, 54));
        topPanel.setPreferredSize(new Dimension(0, 50));
        topPanel.setLayout(new BorderLayout());

        statusLabel = new JLabel(isPlayerTurn ? "YOUR TURN" : "CPU TURN", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        statusLabel.setForeground(Color.WHITE);
        topPanel.add(statusLabel, BorderLayout.CENTER);

        scoreLabel = new JLabel("Match Score: " + GameSession.getInstance().getCurrentGameScore() + "   ");
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        scoreLabel.setForeground(new Color(46, 204, 113));
        topPanel.add(scoreLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 3. LEFT PANEL
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(350, 0));
        leftPanel.setBackground(SIDEBAR_BG);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel historyTitle = new JLabel("MOVE HISTORY", SwingConstants.CENTER);
        historyTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        historyTitle.setForeground(new Color(52, 152, 219));
        historyTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        leftPanel.add(historyTitle, BorderLayout.NORTH);

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setBackground(new Color(50, 50, 50));
        historyArea.setForeground(new Color(240, 240, 240));
        historyArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        historyArea.setMargin(new Insets(15, 15, 15, 15));
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        // 4. CENTER PANEL
        JPanel boardWrapper = new JPanel(new GridBagLayout());
        boardWrapper.setBackground(BOARD_BG);
        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        boardPanel.setPreferredSize(new Dimension(boardSize, boardSize));

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                squares[r][c] = new JButton();
                squares[r][c].setFont(pieceFont);
                squares[r][c].setFocusPainted(false);
                squares[r][c].setBorderPainted(false);
                squares[r][c].setMargin(new Insets(0, 0, 0, 0));

                if ((r + c) % 2 == 0) squares[r][c].setBackground(LIGHT_SQUARE);
                else squares[r][c].setBackground(DARK_SQUARE);

                int finalR = r;
                int finalC = c;
                squares[r][c].addActionListener(e -> handleSquareClick(finalR, finalC));
                boardPanel.add(squares[r][c]);
            }
        }
        boardWrapper.add(boardPanel);
        add(boardWrapper, BorderLayout.CENTER);

        // 5. RIGHT PANEL
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(380, 0));
        rightPanel.setBackground(SIDEBAR_BG);
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel rightContent = new JPanel();
        rightContent.setLayout(new BoxLayout(rightContent, BoxLayout.Y_AXIS));
        rightContent.setBackground(SIDEBAR_BG);

        JLabel infoTitle = new JLabel("MATCH STATS");
        infoTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        infoTitle.setForeground(new Color(255, 215, 0));
        infoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLabelWhite = new JLabel("White Score: " + whiteScore);
        scoreLabelWhite.setFont(new Font("SansSerif", Font.BOLD, 16));
        scoreLabelWhite.setForeground(Color.WHITE);
        scoreLabelWhite.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLabelBlack = new JLabel("Black Score: " + blackScore);
        scoreLabelBlack.setFont(new Font("SansSerif", Font.BOLD, 16));
        scoreLabelBlack.setForeground(Color.WHITE);
        scoreLabelBlack.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightContent.add(infoTitle);
        rightContent.add(Box.createVerticalStrut(15));
        rightContent.add(scoreLabelWhite);
        rightContent.add(Box.createVerticalStrut(5));
        rightContent.add(scoreLabelBlack);
        rightContent.add(Box.createVerticalStrut(40));

        JLabel capturedTitle = new JLabel("CAPTURED PIECES");
        capturedTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        capturedTitle.setForeground(new Color(230, 126, 34));
        capturedTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightContent.add(capturedTitle);
        rightContent.add(Box.createVerticalStrut(20));

        JLabel wLostLbl = new JLabel("White Pieces Lost:");
        wLostLbl.setForeground(Color.LIGHT_GRAY);
        wLostLbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        wLostLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightContent.add(wLostLbl);

        whiteCapturedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        whiteCapturedPanel.setBackground(new Color(60, 63, 65));
        whiteCapturedPanel.setPreferredSize(new Dimension(340, 120));
        whiteCapturedPanel.setMaximumSize(new Dimension(340, 120));
        whiteCapturedPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        rightContent.add(whiteCapturedPanel);

        rightContent.add(Box.createVerticalStrut(25));

        JLabel bLostLbl = new JLabel("Black Pieces Lost:");
        bLostLbl.setForeground(Color.LIGHT_GRAY);
        bLostLbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        bLostLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightContent.add(bLostLbl);

        blackCapturedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        blackCapturedPanel.setBackground(new Color(189, 195, 199));
        blackCapturedPanel.setPreferredSize(new Dimension(340, 120));
        blackCapturedPanel.setMaximumSize(new Dimension(340, 120));
        blackCapturedPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        rightContent.add(blackCapturedPanel);

        rightContent.add(Box.createVerticalGlue());
        rightPanel.add(rightContent, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        btnPanel.setBackground(SIDEBAR_BG);
        btnPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        saveBtn = createStyledButton("Save & Menu", new Color(46, 139, 87));
        saveBtn.setPreferredSize(new Dimension(0, 50));

        resignBtn = createStyledButton("Resign Game", new Color(178, 34, 34));
        resignBtn.setPreferredSize(new Dimension(0, 50));

        JButton backToMenuBtn = createStyledButton("Back to Menu (No Save)", new Color(70, 130, 180));
        backToMenuBtn.setPreferredSize(new Dimension(0, 50));

        saveBtn.addActionListener(e -> {
            if (!isGameOver) {
                GameSession.getInstance().saveCurrentGame(new GameState(board, isPlayerTurn, GameSession.getInstance().getCurrentGameScore()), historyArea.getText());
                new MenuFrame();
                dispose();
            }
        });

        resignBtn.addActionListener(e -> {
            if (isGameOver) {
                showCustomGameOver(false, false, whiteScore, blackScore, historyArea.getText(), true);
            } else {
                showCustomGameOver(false, false, whiteScore, blackScore, historyArea.getText(), false);
            }
        });

        backToMenuBtn.addActionListener(e -> {
            new MenuFrame();
            dispose();
        });

        btnPanel.add(saveBtn);
        btnPanel.add(resignBtn);
        btnPanel.add(backToMenuBtn);

        rightPanel.add(btnPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);

        // --- RESTAURARE ISTORIC DACĂ EXISTĂ ---
        if (loaded != null) {
            historyArea.setText(GameSession.getInstance().getLoadedHistoryLog());
        }

        refreshBoard();
        setVisible(true);

        if (!isPlayerTurn) {
            Timer t = new Timer(1000, e -> performComputerMove());
            t.setRepeats(false); t.start();
        }
    }

    private void addMoveToHistory(Piece p, Position from, Position to) {
        String pieceName = p.getName();
        String fromStr = board.getAlgebraicNotation(from);
        String toStr = board.getAlgebraicNotation(to);
        String log = String.format("%s %-6s: %s -> %s\n", (p.isWhite() ? "W" : "B"), pieceName, fromStr, toStr);
        historyArea.append(log);
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }

    private void handleSquareClick(int r, int c) {
        if (!isPlayerTurn || isGameOver) return;

        Position clickedPos = new Position(r, c);
        Piece clickedPiece = board.getPiece(clickedPos);

        if (selectedPos == null) {
            if (clickedPiece != null && clickedPiece.isWhite() == userPlaysWhite) {
                List<Position> moves = getLegalMoves(clickedPiece);
                if (!moves.isEmpty()) {
                    selectedPos = clickedPos;
                    highlightMoves(moves);
                }
            }
        } else {
            Piece selectedPiece = board.getPiece(selectedPos);
            List<Position> moves = getLegalMoves(selectedPiece);

            if (moves.contains(clickedPos)) {
                Piece targetPiece = board.getPiece(clickedPos);

                addMoveToHistory(selectedPiece, selectedPos, clickedPos);
                board.movePiece(selectedPos, clickedPos);
                handlePawnPromotion(selectedPiece, clickedPos);

                if (targetPiece != null) {
                    onPieceCaptured(targetPiece);
                }

                selectedPos = null;
                resetColors();
                refreshBoard();

                if (isCheckmate(!userPlaysWhite)) {
                    Timer t = new Timer(100, x -> {
                        showCustomGameOver(true, true, whiteScore, blackScore, historyArea.getText(), false);
                    });
                    t.setRepeats(false);
                    t.start();
                    return;
                }

                isPlayerTurn = false;
                statusLabel.setText("COMPUTER THINKING...");
                statusLabel.setForeground(new Color(231, 76, 60));

                Timer t = new Timer(200, e -> performComputerMove());
                t.setRepeats(false); t.start();
            } else {
                selectedPos = null;
                resetColors();
                if (clickedPiece != null && clickedPiece.isWhite() == userPlaysWhite) {
                    List<Position> newMoves = getLegalMoves(clickedPiece);
                    if(!newMoves.isEmpty()){
                        selectedPos = clickedPos;
                        highlightMoves(newMoves);
                    }
                }
            }
        }
    }

    private void performComputerMove() {
        if(isGameOver) return;
        try {
            Random rand = new Random();
            List<Piece> compPieces = new ArrayList<>();
            boolean compIsWhite = !userPlaysWhite;
            for(int r=0; r<8; r++) for(int c=0; c<8; c++) {
                Piece p = board.getPiece(new Position(r,c));
                if(p!=null && p.isWhite() == compIsWhite) compPieces.add(p);
            }
            boolean moved = false;
            while(!compPieces.isEmpty()) {
                int idx = rand.nextInt(compPieces.size());
                Piece p = compPieces.get(idx);
                List<Position> moves = getLegalMoves(p);
                if(!moves.isEmpty()) {
                    Position dest = moves.get(rand.nextInt(moves.size()));

                    Piece targetPiece = board.getPiece(dest);

                    addMoveToHistory(p, p.getPosition(), dest);
                    board.movePiece(p.getPosition(), dest);
                    handlePawnPromotion(p, dest);

                    if (targetPiece != null) {
                        onPieceCaptured(targetPiece);
                    }

                    refreshBoard();

                    if(isCheckmate(userPlaysWhite)) {
                        Timer t = new Timer(100, x -> {
                            showCustomGameOver(false, true, whiteScore, blackScore, historyArea.getText(), false);
                        });
                        t.setRepeats(false);
                        t.start();
                        return;
                    }
                    moved = true;
                    break;
                } else compPieces.remove(idx);
            }
            if(!moved) {
                Timer t = new Timer(100, x -> {
                    showCustomGameOver(true, false, whiteScore, blackScore, historyArea.getText(), false);
                });
                t.setRepeats(false);
                t.start();
            }
        } finally {
            if(isPlayerTurn == false && !isGameOver) {
                isPlayerTurn = true;
                statusLabel.setText("YOUR TURN");
                statusLabel.setForeground(new Color(46, 204, 113));
                refreshBoard();
            }
        }
    }



    private void showCustomGameOver(boolean victory, boolean byCheckmate, int wScore, int bScore, String fullHistory, boolean isReviewMode) {
        if (!isReviewMode) {
            isGameOver = true;
            isPlayerTurn = false;
            GameSession.getInstance().endGame(victory, byCheckmate);
            String resultText = victory ? "VICTORY" : "DEFEAT";
            GameSession.getInstance().saveFinishedGame(resultText, wScore, bScore, fullHistory, board);

            statusLabel.setText(resultText);
            statusLabel.setForeground(victory ? Color.YELLOW : Color.RED);
            saveBtn.setEnabled(false);
            saveBtn.setBackground(Color.GRAY);
            resignBtn.setText("Show Match Report");
            resignBtn.setBackground(new Color(52, 152, 219));
        }

        JDialog dialog = new JDialog(this, "Match Report", true);
        dialog.setUndecorated(true);
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(33, 33, 33));
        panel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 3));

        JPanel headerP = new JPanel();
        headerP.setBackground(new Color(33, 33, 33));
        headerP.setLayout(new BoxLayout(headerP, BoxLayout.Y_AXIS));
        headerP.setBorder(new EmptyBorder(20, 0, 20, 0));

        String titleText = victory ? "VICTORY!" : "DEFEAT";
        Color titleColor = victory ? new Color(255, 215, 0) : new Color(231, 76, 60);

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Serif", Font.BOLD, 48));
        title.setForeground(titleColor);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerP.add(title);

        if (!isReviewMode) {
            JLabel subtitle = new JLabel(byCheckmate ? "by Checkmate" : "by Resignation / Stalemate");
            subtitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
            subtitle.setForeground(Color.LIGHT_GRAY);
            subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            headerP.add(subtitle);
        }
        panel.add(headerP, BorderLayout.NORTH);

        JPanel bodyP = new JPanel();
        bodyP.setBackground(new Color(40, 40, 40));
        bodyP.setLayout(new BoxLayout(bodyP, BoxLayout.Y_AXIS));
        bodyP.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel scoreTitle = new JLabel("FINAL SCORE");
        scoreTitle.setForeground(new Color(46, 204, 113));
        scoreTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        scoreTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel scoreRow = new JPanel(new GridLayout(1, 2, 10, 0));
        scoreRow.setBackground(new Color(40, 40, 40));
        scoreRow.setMaximumSize(new Dimension(500, 50));

        JLabel wLbl = new JLabel("White: " + wScore, SwingConstants.CENTER);
        wLbl.setForeground(Color.WHITE); wLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel bLbl = new JLabel("Black: " + bScore, SwingConstants.CENTER);
        bLbl.setForeground(Color.WHITE); bLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        scoreRow.add(wLbl); scoreRow.add(bLbl);

        JTextArea endHistory = new JTextArea(fullHistory);
        endHistory.setEditable(false);
        endHistory.setFont(new Font("SansSerif", Font.PLAIN, 14));
        endHistory.setBackground(new Color(20, 20, 20));
        endHistory.setForeground(new Color(220, 220, 220));
        JScrollPane scroll = new JScrollPane(endHistory);
        scroll.setPreferredSize(new Dimension(500, 250));
        scroll.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        bodyP.add(scoreTitle);
        bodyP.add(Box.createVerticalStrut(10));
        bodyP.add(scoreRow);
        bodyP.add(Box.createVerticalStrut(20));
        bodyP.add(scroll);
        panel.add(bodyP, BorderLayout.CENTER);

        JPanel footerP = new JPanel();
        footerP.setBackground(new Color(33, 33, 33));
        footerP.setLayout(new BoxLayout(footerP, BoxLayout.Y_AXIS));
        footerP.setBorder(new EmptyBorder(20, 0, 20, 0));

        JPanel btnRow = new JPanel(new FlowLayout());
        btnRow.setBackground(new Color(33, 33, 33));

        JButton viewBoardBtn = createStyledButton("View Final Board", new Color(243, 156, 18));
        viewBoardBtn.setPreferredSize(new Dimension(180, 45));
        viewBoardBtn.addActionListener(e -> dialog.dispose());

        JButton menuBtn = createStyledButton("Back to Menu", new Color(52, 152, 219));
        menuBtn.setPreferredSize(new Dimension(180, 45));
        menuBtn.addActionListener(e -> {
            dialog.dispose();
            new MenuFrame();
            dispose();
        });

        JButton exitBtn = createStyledButton("Exit App", new Color(192, 57, 43));
        exitBtn.setPreferredSize(new Dimension(180, 45));
        exitBtn.addActionListener(e -> System.exit(0));

        btnRow.add(viewBoardBtn);
        btnRow.add(menuBtn);
        btnRow.add(exitBtn);

        footerP.add(btnRow);
        panel.add(footerP, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private List<Position> getLegalMoves(Piece p) {
        List<Position> pseudoLegalMoves = p.getValidMoves(board);
        List<Position> legalMoves = new ArrayList<>();
        for (Position dest : pseudoLegalMoves) {
            Position start = p.getPosition();
            Piece capturedPiece = board.getPiece(dest);
            board.setPiece(dest, p);
            board.setPiece(start, null);
            Position kingPos = board.findKing(p.isWhite());
            boolean inCheck = false;
            if (kingPos != null) inCheck = board.isSquareAttacked(kingPos, !p.isWhite());
            board.setPiece(start, p);
            board.setPiece(dest, capturedPiece);
            if (!inCheck) legalMoves.add(dest);
        }
        return legalMoves;
    }

    private void handlePawnPromotion(Piece p, Position pos) {
        if (p.getName().equals("Pawn")) {
            if ((p.isWhite() && pos.row() == 0) || (!p.isWhite() && pos.row() == 7)) {
                String type = "Queen";
                if (p.isWhite() == userPlaysWhite) type = showCustomPromotionDialog(p.isWhite());
                Piece promoted = PieceFactory.createPiece(type, pos, p.isWhite());
                board.setPiece(pos, promoted);
                refreshBoard();
            }
        }
    }

    private String showCustomPromotionDialog(boolean isWhite) {
        JDialog dialog = new JDialog(this, "Promote Pawn", true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 180);
        dialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(44, 62, 80));
        panel.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2));
        JLabel title = new JLabel("CHOOSE PROMOTION", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(15, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);
        JPanel btnPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        btnPanel.setBackground(new Color(44, 62, 80));
        btnPanel.setBorder(new EmptyBorder(0, 15, 15, 15));
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        String[] symbols = isWhite ? new String[]{"♕", "♖", "♗", "♘"} : new String[]{"♛", "♜", "♝", "♞"};
        final String[] selection = {"Queen"};
        for (int i = 0; i < 4; i++) {
            String role = options[i];
            JButton btn = new JButton(symbols[i]);
            btn.setFont(new Font("Serif", Font.BOLD, 40));
            btn.setBackground(new Color(52, 73, 94));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                selection[0] = role;
                dialog.dispose();
            });
            btnPanel.add(btn);
        }
        panel.add(btnPanel, BorderLayout.CENTER);
        dialog.add(panel);
        dialog.setVisible(true);
        return selection[0];
    }

    private boolean isCheckmate(boolean isWhiteKing) {
        Position kingPos = board.findKing(isWhiteKing);
        if (!board.isSquareAttacked(kingPos, !isWhiteKing)) return false;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(new Position(r, c));
                if (p != null && p.isWhite() == isWhiteKing) {
                    List<Position> legalMoves = getLegalMoves(p);
                    if (!legalMoves.isEmpty()) return false;
                }
            }
        }
        return true;
    }

    private void refreshBoard() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(new Position(r, c));
                if (p != null) {
                    String symbol = getSolidSymbol(p.getName());
                    squares[r][c].setText(symbol);
                    squares[r][c].setForeground(p.isWhite() ? Color.WHITE : Color.BLACK);
                } else {
                    squares[r][c].setText("");
                }
            }
        }
    }

    private String getSolidSymbol(String name) {
        return switch (name) {
            case "King" -> "\u265A"; case "Queen" -> "\u265B"; case "Rook" -> "\u265C";
            case "Bishop" -> "\u265D"; case "Knight" -> "\u265E"; case "Pawn" -> "\u265F";
            default -> "?";
        };
    }

    private void highlightMoves(List<Position> moves) {
        resetColors();
        squares[selectedPos.row()][selectedPos.col()].setBackground(SELECTED_COLOR);
        for (Position p : moves) squares[p.row()][p.col()].setBackground(MOVE_COLOR);
    }

    private void resetColors() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if ((r + c) % 2 == 0) squares[r][c].setBackground(LIGHT_SQUARE);
                else squares[r][c].setBackground(DARK_SQUARE);
            }
        }
    }

    private void setAppIcon() {
        try {
            ImageIcon icon = new ImageIcon("images.png.png");
            if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) icon = new ImageIcon("images.png");
            setIconImage(icon.getImage());
        } catch (Exception e) { }
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    @Override public void onBoardUpdate() { refreshBoard(); }

    @Override public void onPieceCaptured(Piece p) {
        if (p.isWhite()) blackScore += p.getPoints();
        else whiteScore += p.getPoints();

        scoreLabelWhite.setText("White Score: " + whiteScore);
        scoreLabelBlack.setText("Black Score: " + blackScore);

        if(p.isWhite() != userPlaysWhite) {
            GameSession.getInstance().addGamePoints(p.getPoints());
            scoreLabel.setText("Match Score: " + GameSession.getInstance().getCurrentGameScore() + "   ");
        }

        JLabel pieceLbl = new JLabel(getSolidSymbol(p.getName()));
        pieceLbl.setFont(new Font("Serif", Font.PLAIN, 28));

        if (p.isWhite()) {
            pieceLbl.setForeground(Color.WHITE);
            whiteCapturedPanel.add(pieceLbl);
            whiteCapturedPanel.revalidate();
            whiteCapturedPanel.repaint();
        } else {
            pieceLbl.setForeground(Color.BLACK);
            blackCapturedPanel.add(pieceLbl);
            blackCapturedPanel.revalidate();
            blackCapturedPanel.repaint();
        }
    }
}