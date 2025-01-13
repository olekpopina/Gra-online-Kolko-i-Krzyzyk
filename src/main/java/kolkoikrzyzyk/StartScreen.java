package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StartScreen extends JFrame {
    private final String loggedInUser; // Збереження імені залогіненого користувача

    public StartScreen(String username) {
        this.loggedInUser = username; // Ім'я залогіненого користувача
        setTitle("Kółko i Krzyżyk - Zalogowano jako: " + (username != null ? username : "Gość"));
        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel("/images/StartScreen.jpg");
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Верхня панель: логін користувача + топ-10 гравців
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel userPanel = createUserPanel();
        JPanel topPlayersPanel = createTopPlayersPanel();

        topPanel.add(userPanel);
        topPanel.add(topPlayersPanel);

        backgroundPanel.add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = createButtonPanel();
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER);

        setJMenuBar(createMenuBar());

        setVisible(true);
    }

    private JPanel createUserPanel() {
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 0, 0, 150)); // Чорний колір з 150 рівнем прозорості (0-255)
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        userPanel.setOpaque(false); // Залишаємо прозорість панелі
        JLabel userLabel = new JLabel("Zalogowano jako: " + (loggedInUser != null ? loggedInUser : "Gość"));
        userLabel.setFont(new Font("Arial", Font.BOLD, 22));
        userLabel.setForeground(Color.YELLOW);
        userPanel.add(userLabel);
        return userPanel;
    }

    private JPanel createTopPlayersPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Najlepsi gracze:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        List<String> topPlayers = DatabaseManager.getTopPlayers();
        for (String player : topPlayers) {
            JLabel playerLabel = new JLabel(player);
            playerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            playerLabel.setForeground(Color.WHITE);
            playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(playerLabel);
        }
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JButton localGameButton = createStyledButton("Gra na jednym komputerze");
        gbc.gridy = 0;
        panel.add(localGameButton, gbc);
        localGameButton.addActionListener(e -> {
            dispose();
            LocalGame game = new LocalGame();
            game.setLoggedInUser(loggedInUser);
            game.updateWindowTitle();
        });

        JButton vsComputerButton = createStyledButton("Gra przeciwko komputerowi");
        gbc.gridy = 1;
        panel.add(vsComputerButton, gbc);
        vsComputerButton.addActionListener(e -> {
            dispose();
            VsComputerGame game = new VsComputerGame();
            game.setLoggedInUser(loggedInUser);
            game.updateWindowTitle();
        });

        JButton serverButton = createStyledButton("Gra jako serwer");
        gbc.gridy = 2;
        panel.add(serverButton, gbc);
        serverButton.addActionListener(e -> {
            dispose();
            ServerGame game = new ServerGame();
            game.setLoggedInUser(loggedInUser);
            game.updateWindowTitle();
        });

        JButton clientButton = createStyledButton("Gra jako klient");
        gbc.gridy = 3;
        panel.add(clientButton, gbc);
        clientButton.addActionListener(e -> {
            String ip = JOptionPane.showInputDialog("Podaj IP serwera:");
            dispose();
            ClientGame game = new ClientGame(ip);
            game.setLoggedInUser(loggedInUser);
            game.updateWindowTitle();
        });

        JButton loginButton = createStyledButton("Zaloguj się");
        gbc.gridy = 4;
        panel.add(loginButton, gbc);
        loginButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog("Podaj nazwę użytkownika:");
            String password = JOptionPane.showInputDialog("Podaj hasło:");
            if (DatabaseManager.authenticateUser(username, password)) {
                JOptionPane.showMessageDialog(this, "Zalogowano jako: " + username);
                dispose();
                new StartScreen(username);
            } else {
                JOptionPane.showMessageDialog(this, "Nieprawidłowy login lub hasło!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton logoutButton = createStyledButton("Wyloguj się");
        gbc.gridy = 5;
        panel.add(logoutButton, gbc);
        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Wylogowano!");
            dispose();
            new StartScreen(null);
        });

        JButton exitButton = createStyledButton("Wyjście");
        gbc.gridy = 6;
        panel.add(exitButton, gbc);
        exitButton.addActionListener(e -> System.exit(0));

        return panel;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu userMenu = new JMenu("Korzystnik");

        JMenuItem registerMenuItem = new JMenuItem("Zarejestruj nowego użytkownika");
        registerMenuItem.addActionListener(e -> showRegisterDialog());
        userMenu.add(registerMenuItem);

        JMenuItem changePasswordMenuItem = new JMenuItem("Zmień hasło");
        changePasswordMenuItem.setEnabled(loggedInUser != null);
        changePasswordMenuItem.addActionListener(e -> showChangePasswordDialog());
        userMenu.add(changePasswordMenuItem);

        JMenuItem viewStatsMenuItem = new JMenuItem("Pokaż statystyki");
        viewStatsMenuItem.setEnabled(loggedInUser != null);
        viewStatsMenuItem.addActionListener(e -> showUserStats());
        userMenu.add(viewStatsMenuItem);

        menuBar.add(userMenu);
        return menuBar;
    }


    private void showRegisterDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Podaj nazwę użytkownika:", usernameField,
                "Podaj hasło:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Rejestracja nowego użytkownika", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (DatabaseManager.registerUser(username, password)) {
                JOptionPane.showMessageDialog(this, "Konto zostało utworzone!", "Sukces", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Użytkownik już istnieje!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showChangePasswordDialog() {
        JPasswordField oldPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        Object[] message = {
                "Podaj stare hasło:", oldPasswordField,
                "Podaj nowe hasło:", newPasswordField,
                "Potwierdź nowe hasło:", confirmPasswordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Zmień hasło", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Nowe hasła nie pasują do siebie!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (DatabaseManager.changePassword(loggedInUser, oldPassword, newPassword)) {
                JOptionPane.showMessageDialog(this, "Hasło zostało zmienione!", "Sukces", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Nie udało się zmienić hasła!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(240, 240, 240));
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button.setFocusPainted(false);
        button.setOpaque(true);
        return button;
    }

    private void showUserStats() {
        if (loggedInUser == null) {
            JOptionPane.showMessageDialog(this, "Zaloguj się, aby zobaczyć statystyki.", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserStats stats = DatabaseManager.getUserStats(loggedInUser);
        if (stats == null) {
            JOptionPane.showMessageDialog(this, "Nie udało się pobrać statystyk użytkownika.", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String message = String.format(
                "<html><body>" +
                        "<h2>Statystyki dla użytkownika: %s</h2>" +
                        "<ul>" +
                        "<li>Rozegrane gry (online): %d</li>" +
                        "<li>Wygrane gry (online): %d</li>" +
                        "<li>Przegrane gry (online): %d</li>" +
                        "<li>Stosunek wygranych do rozegranych gier (online): %.2f</li>" +
                        "<li>Wygrane gry przeciw komputerowi: %d</li>" +
                        "<li>Rozegrane gry lokalne: %d</li>" +
                        "</ul>" +
                        "</body></html>",
                loggedInUser,
                stats.getGamesPlayed(),
                stats.getWins(),
                stats.getLosses(),
                stats.getWinRatio(),
                stats.getGamesVsBot(),
                stats.getGamesLocal()
        );

        JOptionPane.showMessageDialog(this, message, "Statystyki użytkownika", JOptionPane.INFORMATION_MESSAGE);
    }

}
