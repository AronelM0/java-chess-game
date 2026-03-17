package gui;

import singleton.GameSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    private final Color BG_COLOR = new Color(44, 62, 80);
    private final Color INPUT_BG = new Color(52, 73, 94);
    private final Color ACCENT_COLOR = new Color(39, 174, 96); // Verde Mate
    private final Color TEXT_COLOR = new Color(236, 240, 241);
    private final Font MAIN_FONT = new Font("SansSerif", Font.PLAIN, 14);

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("ChessMate - Login");
        setAppIcon();
        setSize(420, 580);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        add(mainPanel);

        // --- LOGO ---
        JLabel logoLabel = new JLabel("♞");
        logoLabel.setFont(new Font("Serif", Font.BOLD, 80));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("CHESSMATE");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(new Color(241, 196, 15)); // Gold
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(logoLabel);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(40));

        // --- INPUTS ---
        JLabel emailLbl = new JLabel("Username / Email:");
        emailLbl.setFont(MAIN_FONT);
        emailLbl.setForeground(TEXT_COLOR);
        emailLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        emailField = createStyledTextField();
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passLbl = new JLabel("Password:");
        passLbl.setFont(MAIN_FONT);
        passLbl.setForeground(TEXT_COLOR);
        passLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = createStyledPasswordField();
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(emailLbl);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(emailField);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(passLbl);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(30));

        // --- BUTOANE ---
        JButton loginBtn = createStyledButton("SIGN IN", ACCENT_COLOR);
        JButton registerBtn = createStyledButton("Create New Account", new Color(52, 152, 219));
        JButton exitBtn = createStyledButton("EXIT", new Color(192, 57, 43));

        // Acțiuni Login
        ActionListener loginAction = e -> performLogin();
        loginBtn.addActionListener(loginAction);

        // ENTER KEY SUPPORT PENTRU LOGIN (Butonul principal)
        getRootPane().setDefaultButton(loginBtn);

        registerBtn.addActionListener(e -> showRegisterDialog());
        exitBtn.addActionListener(e -> System.exit(0));

        mainPanel.add(loginBtn);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(registerBtn);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(exitBtn);

        setVisible(true);
    }

    // --- FEREASTRĂ REGISTER (MODERNĂ) ---
    private void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "Create Account", true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 380);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_COLOR);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                new EmptyBorder(30, 40, 30, 40)
        ));

        JLabel title = new JLabel("Join ChessMate");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitle = new JLabel("Create your profile");
        subTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subTitle.setForeground(Color.LIGHT_GRAY);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField regEmail = createStyledTextField();
        JPasswordField regPass = createStyledPasswordField();

        JLabel l1 = new JLabel("Enter Username:");
        l1.setForeground(Color.WHITE);
        l1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel l2 = new JLabel("Choose Password:");
        l2.setForeground(Color.WHITE);
        l2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton createBtn = createStyledButton("REGISTER NOW", new Color(52, 152, 219));
        JButton cancelBtn = createStyledButton("Cancel", new Color(149, 165, 166));

        // Logica de înregistrare
        ActionListener registerAction = e -> {
            String eVal = regEmail.getText();
            String pVal = new String(regPass.getPassword());

            if(eVal.isEmpty() || pVal.isEmpty()) {
                showCustomMessage(dialog, "Error", "Please fill all fields!", true);
                return;
            }

            if(GameSession.getInstance().register(eVal, pVal)) {
                // SUCCESS
                dialog.dispose();
                showCustomMessage(this, "Success", "Account Created!\nYou can now login.", false);
            } else {
                showCustomMessage(dialog, "Error", "Account already exists.", true);
            }
        };

        createBtn.addActionListener(registerAction);
        cancelBtn.addActionListener(e -> dialog.dispose());

        // SUPORT ENTER ÎN FEREASTRA DE REGISTER
        dialog.getRootPane().setDefaultButton(createBtn);

        panel.add(title);
        panel.add(subTitle);
        panel.add(Box.createVerticalStrut(30));
        panel.add(l1);
        panel.add(Box.createVerticalStrut(5));
        panel.add(regEmail);
        panel.add(Box.createVerticalStrut(15));
        panel.add(l2);
        panel.add(Box.createVerticalStrut(5));
        panel.add(regPass);
        panel.add(Box.createVerticalStrut(30));
        panel.add(createBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(cancelBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // --- MESAJ PERSONALIZAT ---
    private void showCustomMessage(Component parent, String titleText, String msgText, boolean isError) {
        JDialog msgDialog = new JDialog();
        msgDialog.setUndecorated(true);
        msgDialog.setSize(350, 200);
        msgDialog.setLocationRelativeTo(parent);

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(44, 62, 80));
        Color borderColor = isError ? new Color(231, 76, 60) : new Color(46, 204, 113);
        p.setBorder(BorderFactory.createLineBorder(borderColor, 2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.insets = new Insets(10, 20, 10, 20);

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(borderColor);
        gbc.gridy = 0;
        p.add(title, gbc);

        JTextArea msg = new JTextArea(msgText);
        msg.setFont(new Font("SansSerif", Font.PLAIN, 16));
        msg.setForeground(Color.WHITE);
        msg.setBackground(new Color(44, 62, 80));
        msg.setEditable(false);
        msg.setWrapStyleWord(true);
        msg.setLineWrap(true);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        msg.setBorder(null);

        gbc.gridy = 1;
        p.add(msg, gbc);

        JButton okBtn = createStyledButton("OK", borderColor);
        okBtn.setPreferredSize(new Dimension(100, 40));
        okBtn.addActionListener(e -> msgDialog.dispose());


        msgDialog.getRootPane().setDefaultButton(okBtn);
        okBtn.requestFocusInWindow();


        gbc.gridy = 2;
        gbc.insets = new Insets(20, 20, 10, 20);
        p.add(okBtn, gbc);

        msgDialog.add(p);
        msgDialog.setVisible(true);
    }

    private void performLogin() {
        String email = emailField.getText();
        String pass = new String(passwordField.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            showCustomMessage(this, "Error", "Please fill in all fields.", true);
            return;
        }

        if (GameSession.getInstance().login(email, pass)) {
            new MenuFrame();
            dispose();
        } else {
            showCustomMessage(this, "Error", "Invalid credentials.", true);
        }
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        styleField(field);
        return field;
    }

    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(300, 40));
        field.setPreferredSize(new Dimension(300, 40));
        field.setFont(MAIN_FONT);
        field.setBackground(INPUT_BG);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(300, 45));
        btn.setPreferredSize(new Dimension(300, 45));
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { btn.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent evt) { btn.setBackground(bg); }
        });
        return btn;
    }

    private void setAppIcon() {
        try {
            ImageIcon icon = new ImageIcon("images.png.png");
            if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) icon = new ImageIcon("images.png");
            setIconImage(icon.getImage());
        } catch (Exception e) { }
    }
}