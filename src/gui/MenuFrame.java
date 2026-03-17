package gui;

import models.GameRecord;
import singleton.DataManager;
import singleton.GameSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

public class MenuFrame extends JFrame {

    private final Color BG_COLOR = new Color(47, 54, 64);
    private final Color BTN_COLOR = new Color(230, 126, 34);
    private final Color ACCENT_COLOR = new Color(52, 152, 219);
    private final Color HISTORY_BTN_COLOR = new Color(155, 89, 182); // Violet

    private JLabel statsLabel;

    public MenuFrame() {
        setTitle("ChessMate - Main Menu");
        setAppIcon();
        setSize(900, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // --- 1. TOP BAR CU RESET STATS ---
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(BG_COLOR);

        JButton resetStatsBtn = new JButton("Reset Stats");
        resetStatsBtn.setBackground(new Color(192, 57, 43));
        resetStatsBtn.setForeground(Color.WHITE);
        resetStatsBtn.setFocusPainted(false);
        resetStatsBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        resetStatsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        resetStatsBtn.addActionListener(e -> showCustomResetDialog());
        topBar.add(resetStatsBtn);
        add(topBar, BorderLayout.NORTH);

        // --- 2. MAIN PANEL ---
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BG_COLOR);
        add(mainPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("♛ CHESSMATE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI Symbol", Font.BOLD, 55));
        titleLabel.setForeground(new Color(251, 197, 49));
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        String userEmail = GameSession.getInstance().getUserEmail();
        String userName = userEmail.contains("@") ? userEmail.split("@")[0] : userEmail;

        JLabel welcomeLabel = new JLabel("Welcome back, " + userName + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));
        welcomeLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        mainPanel.add(welcomeLabel, gbc);

        statsLabel = new JLabel("", SwingConstants.CENTER);
        statsLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        statsLabel.setForeground(new Color(46, 204, 113));
        updateStatsLabel();
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        mainPanel.add(statsLabel, gbc);

        // BUTOANELE MENIULUI
        gbc.insets = new Insets(8, 10, 8, 10);

        JButton newGameBtn = createStyledButton("Start New Game", BTN_COLOR);
        newGameBtn.addActionListener(e -> showNewGameOptions());
        gbc.gridy = 3;
        mainPanel.add(newGameBtn, gbc);

        JButton continueBtn = createStyledButton("Continue Saved Game", BTN_COLOR);
        continueBtn.addActionListener(e -> {
            if (GameSession.getInstance().loadSavedGame()) {
                new GameFrame();
                dispose();
            } else {
                showCustomMessage("Info", "No saved game found!");
            }
        });
        gbc.gridy = 4;
        mainPanel.add(continueBtn, gbc);

        JButton leaderboardBtn = createStyledButton("Hall of Fame", ACCENT_COLOR);
        leaderboardBtn.addActionListener(e -> showStylishLeaderboard());
        gbc.gridy = 5;
        mainPanel.add(leaderboardBtn, gbc);

        // --- 3. BUTONUL DE ISTORIC ---
        JButton historyBtn = createStyledButton("Match History", HISTORY_BTN_COLOR);
        historyBtn.addActionListener(e -> showHistoryDialog());
        gbc.gridy = 6;
        mainPanel.add(historyBtn, gbc);

        JButton logoutBtn = createStyledButton("Logout", new Color(192, 57, 43));
        logoutBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });
        gbc.gridy = 7;
        mainPanel.add(logoutBtn, gbc);

        setVisible(true);
    }

    // --- METODE PENTRU FERESTRE ---

    private void showHistoryDialog() {
        JDialog dialog = new JDialog(this, "Match History", true);
        dialog.setUndecorated(true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(34, 47, 62));
        JLabel headerLbl = new JLabel("MATCH HISTORY");
        headerLbl.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLbl.setForeground(new Color(155, 89, 182));
        headerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        headerPanel.add(headerLbl);
        dialog.add(headerPanel, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(44, 62, 80));

        List<GameRecord> history = GameSession.getInstance().getMyHistory();

        if (history.isEmpty()) {
            JLabel emptyLbl = new JLabel("No games played yet.");
            emptyLbl.setForeground(Color.LIGHT_GRAY);
            emptyLbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
            emptyLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createVerticalStrut(50));
            listPanel.add(emptyLbl);
        } else {
            for (GameRecord record : history) {
                JPanel row = new JPanel(new BorderLayout());
                row.setMaximumSize(new Dimension(460, 60));
                row.setBackground(new Color(52, 73, 94));
                row.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0,0,1,0, new Color(44,62,80)),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                row.setCursor(new Cursor(Cursor.HAND_CURSOR));

                JLabel dateLbl = new JLabel(record.getTimestamp());
                dateLbl.setForeground(Color.GRAY);
                dateLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));

                JLabel resultLbl = new JLabel(record.getResult());
                resultLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
                if (record.getResult().equals("VICTORY")) resultLbl.setForeground(new Color(46, 204, 113));
                else resultLbl.setForeground(new Color(231, 76, 60));

                JLabel scoreLbl = new JLabel("W: " + record.getWhiteScore() + " | B: " + record.getBlackScore());
                scoreLbl.setForeground(Color.WHITE);
                scoreLbl.setFont(new Font("SansSerif", Font.PLAIN, 14));

                JPanel centerP = new JPanel(new GridLayout(2,1));
                centerP.setBackground(new Color(52, 73, 94));
                centerP.add(resultLbl);
                centerP.add(dateLbl);

                row.add(centerP, BorderLayout.WEST);
                row.add(scoreLbl, BorderLayout.EAST);

                row.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showGameDetails(record);
                    }
                });

                listPanel.add(row);
                listPanel.add(Box.createVerticalStrut(5));
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(231, 76, 60));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dialog.dispose());
        dialog.add(closeBtn, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showGameDetails(GameRecord record) {
        JDialog d = new JDialog(this, "Game Details", true);
        d.setUndecorated(true);
        d.setSize(500, 500);
        d.setLocationRelativeTo(this);

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(33, 33, 33));
        p.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        JLabel title = new JLabel(record.getResult() + " (" + record.getTimestamp() + ")", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(record.getResult().equals("VICTORY") ? new Color(46, 204, 113) : new Color(231, 76, 60));
        title.setBorder(new EmptyBorder(15, 0, 15, 0));

        JTextArea logArea = new JTextArea(record.getMovesLog());
        logArea.setEditable(false);
        logArea.setBackground(new Color(20, 20, 20));
        logArea.setForeground(Color.LIGHT_GRAY);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setMargin(new Insets(10,10,10,10));

        JButton backBtn = new JButton("Back");
        backBtn.setBackground(new Color(52, 152, 219));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> d.dispose());

        p.add(title, BorderLayout.NORTH);
        p.add(new JScrollPane(logArea), BorderLayout.CENTER);
        p.add(backBtn, BorderLayout.SOUTH);

        d.add(p);
        d.setVisible(true);
    }

    private void showCustomResetDialog() {
        JDialog dialog = new JDialog(this, "Reset Confirmation", true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 220);
        dialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(44, 62, 80));
        panel.setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 3));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        JLabel title = new JLabel("WARNING!", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(231, 76, 60));
        gbc.gridy = 0; panel.add(title, gbc);
        JLabel msg = new JLabel("<html><center>Are you sure you want to<br>RESET your score to 0?</center></html>", SwingConstants.CENTER);
        msg.setFont(new Font("SansSerif", Font.PLAIN, 16));
        msg.setForeground(Color.WHITE);
        gbc.gridy = 1; panel.add(msg, gbc);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(new Color(44, 62, 80));
        JButton yesBtn = new JButton("YES, RESET");
        yesBtn.setBackground(new Color(192, 57, 43));
        yesBtn.setForeground(Color.WHITE);
        yesBtn.setFocusPainted(false);
        yesBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        yesBtn.setPreferredSize(new Dimension(120, 40));
        yesBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        JButton noBtn = new JButton("Cancel");
        noBtn.setBackground(new Color(127, 140, 141));
        noBtn.setForeground(Color.WHITE);
        noBtn.setFocusPainted(false);
        noBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        noBtn.setPreferredSize(new Dimension(120, 40));
        noBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        yesBtn.addActionListener(e -> {
            GameSession.getInstance().resetAccountStats();
            updateStatsLabel();
            dialog.dispose();
            showCustomMessage("Success", "Stats have been reset.");
        });
        noBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(yesBtn); btnPanel.add(noBtn);
        gbc.gridy = 2; gbc.insets = new Insets(20, 10, 10, 10); panel.add(btnPanel, gbc);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void updateStatsLabel() {
        int totalPoints = GameSession.getInstance().getTotalAccountScore();
        int totalGames = GameSession.getInstance().getGamesPlayed();
        statsLabel.setText("Points: " + totalPoints + "  |  Games Played: " + totalGames);
    }

    private void showCustomMessage(String titleText, String messageText) {
        JDialog dialog = new JDialog(this, titleText, true);
        dialog.setUndecorated(true);
        dialog.setSize(350, 180);
        dialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(44, 62, 80));
        panel.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(52, 152, 219));
        gbc.gridy = 0; panel.add(title, gbc);
        JLabel msg = new JLabel(messageText, SwingConstants.CENTER);
        msg.setFont(new Font("SansSerif", Font.PLAIN, 16));
        msg.setForeground(Color.WHITE);
        gbc.gridy = 1; panel.add(msg, gbc);
        JButton okBtn = new JButton("OK");
        okBtn.setBackground(new Color(52, 152, 219));
        okBtn.setForeground(Color.WHITE);
        okBtn.setFocusPainted(false);
        okBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        okBtn.setPreferredSize(new Dimension(100, 35));
        okBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        okBtn.addActionListener(e -> dialog.dispose());
        gbc.gridy = 2; gbc.insets = new Insets(20, 10, 10, 10); panel.add(okBtn, gbc);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showNewGameOptions() {
        JDialog dialog = new JDialog(this, "New Game Setup", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(44, 62, 80));
        panel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel label = new JLabel("CHOOSE YOUR SIDE");
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        label.setForeground(new Color(255, 215, 0));
        panel.add(label, gbc);
        gbc.gridwidth = 1; gbc.gridy = 1;
        JButton whiteBtn = new JButton("<html><center><span style='font-size:30px'>♔</span><br>Play as WHITE</center></html>");
        whiteBtn.setPreferredSize(new Dimension(180, 100));
        whiteBtn.setBackground(Color.WHITE);
        whiteBtn.setForeground(Color.BLACK);
        whiteBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        whiteBtn.setFocusPainted(false);
        whiteBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
        whiteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JButton blackBtn = new JButton("<html><center><span style='font-size:30px; color:white'>♚</span><br>Play as BLACK</center></html>");
        blackBtn.setPreferredSize(new Dimension(180, 100));
        blackBtn.setBackground(Color.BLACK);
        blackBtn.setForeground(Color.WHITE);
        blackBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        blackBtn.setFocusPainted(false);
        blackBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
        blackBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        whiteBtn.addActionListener(e -> {
            dialog.dispose();
            GameSession.getInstance().startNewGame(true);
            new GameFrame();
            dispose();
        });
        blackBtn.addActionListener(e -> {
            dialog.dispose();
            GameSession.getInstance().startNewGame(false);
            new GameFrame();
            dispose();
        });
        gbc.gridx = 0; panel.add(whiteBtn, gbc);
        gbc.gridx = 1; panel.add(blackBtn, gbc);
        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showStylishLeaderboard() {
        JDialog dialog = new JDialog(this, "ChessMate - Hall of Fame", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(34, 47, 62));
        JLabel headerLbl = new JLabel("TOP PLAYERS");
        headerLbl.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLbl.setForeground(new Color(255, 215, 0));
        headerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        headerPanel.add(headerLbl);
        dialog.add(headerPanel, BorderLayout.NORTH);
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(44, 62, 80));
        String currentUserEmail = GameSession.getInstance().getUserEmail();
        String currentUserName = currentUserEmail.contains("@") ? currentUserEmail.split("@")[0] : currentUserEmail;
        List<Map.Entry<String, Integer>> players = DataManager.getInstance().getTopPlayers();
        int rank = 1;
        for (Map.Entry<String, Integer> player : players) {
            JPanel playerRow = new JPanel(new BorderLayout());
            playerRow.setMaximumSize(new Dimension(460, 55));
            boolean isMe = player.getKey().equals(currentUserName);
            if (isMe) {
                playerRow.setBackground(new Color(39, 174, 96));
                playerRow.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.WHITE, 2),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            } else {
                playerRow.setBackground(new Color(52, 73, 94));
                playerRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(44, 62, 80)));
            }
            String rankPrefix = "   #" + rank + "   ";
            Color rankColor = Color.WHITE;
            if(rank == 1) { rankPrefix = " ♛ 1   "; rankColor = new Color(255, 215, 0); }
            else if(rank == 2) { rankPrefix = " ♛ 2   "; rankColor = new Color(192, 192, 192); }
            else if(rank == 3) { rankPrefix = " ♛ 3   "; rankColor = new Color(205, 127, 50); }
            JLabel rankLbl = new JLabel(rankPrefix);
            rankLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
            rankLbl.setForeground(rankColor);
            String displayName = player.getKey() + (isMe ? " (You)" : "");
            JLabel nameLbl = new JLabel(displayName);
            nameLbl.setFont(new Font("SansSerif", isMe ? Font.BOLD : Font.PLAIN, 18));
            nameLbl.setForeground(Color.WHITE);
            JLabel scoreLbl = new JLabel(player.getValue() + " pts   ");
            scoreLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
            scoreLbl.setForeground(isMe ? Color.WHITE : new Color(46, 204, 113));
            playerRow.add(rankLbl, BorderLayout.WEST);
            playerRow.add(nameLbl, BorderLayout.CENTER);
            playerRow.add(scoreLbl, BorderLayout.EAST);
            listPanel.add(playerRow);
            if (!isMe) listPanel.add(Box.createVerticalStrut(5));
            rank++;
        }
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        dialog.add(scrollPane, BorderLayout.CENTER);
        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(231, 76, 60));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dialog.dispose());
        dialog.add(closeBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void setAppIcon() {
        try {
            ImageIcon icon = new ImageIcon("images.png.png");
            if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                icon = new ImageIcon("images.png");
            }
            setIconImage(icon.getImage());
        } catch (Exception e) { }
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(350, 55));
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}