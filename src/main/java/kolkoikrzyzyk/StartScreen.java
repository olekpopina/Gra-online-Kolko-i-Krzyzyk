package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class StartScreen extends JFrame {
    private String loggedInUser = null;
    public StartScreen() {
        setTitle("Witamy w grze Kolko i Krzyzyk");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Панель з фоном
        BackgroundPanel backgroundPanel = new BackgroundPanel("/images/tlo.png");
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        GridBagConstraints gbc = new GridBagConstraints();

        // Додаємо список найкращих гравців
        JPanel topPanel = createTopPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(topPanel, gbc);

        // Додаємо панель з кнопками
        JPanel buttonPanel = createButtonPanel();
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        backgroundPanel.add(buttonPanel, gbc);

        // Додаємо кнопку "Вийти" внизу
        JButton exitButton = createStyledButton("Wychodź z gry");
        exitButton.addActionListener(e -> System.exit(0));
        gbc.gridy = 2;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        backgroundPanel.add(exitButton, gbc);

        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Najlepsi gracze:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Отримуємо топ-10 гравців
        List<String> topPlayers = DatabaseManager.getTopPlayers();

        JPanel playerListPanel = new JPanel();
        playerListPanel.setOpaque(false);
        playerListPanel.setLayout(new BoxLayout(playerListPanel, BoxLayout.Y_AXIS));

        for (String player : topPlayers) {
            JLabel playerLabel = new JLabel(player);
            playerLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            playerLabel.setForeground(Color.YELLOW);
            playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            playerListPanel.add(playerLabel);
        }

        panel.add(Box.createVerticalStrut(10)); // Зменшений відступ
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));  // Менший відступ перед списком
        panel.add(playerListPanel);
        panel.add(Box.createVerticalStrut(10)); // Зменшений відступ після списку

        return panel;
    }



    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Зменшені відступи між кнопками
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Кнопка "Локальна гра"
        JButton localGameButton = createStyledButton("Gra na jednym komputerze");
        gbc.gridy = 0;
        panel.add(localGameButton, gbc);
        localGameButton.addActionListener(e -> {
            dispose();
            LocalGame game = new LocalGame();
            game.setLoggedInUser(loggedInUser);
            game.setGameMode("local");
        });

        // Кнопка "Проти комп'ютера"
        JButton vsComputerButton = createStyledButton("Gra przeciw komputerowi");
        gbc.gridy = 1;
        panel.add(vsComputerButton, gbc);
        vsComputerButton.addActionListener(e -> {
            dispose();
            VsComputerGame game = new VsComputerGame();
            game.setLoggedInUser(loggedInUser);
            game.setGameMode("vs_bot");
        });

        // Кнопка "Гра як сервер"
        JButton serverButton = createStyledButton("Gra jako serwer");
        gbc.gridy = 2;
        panel.add(serverButton, gbc);
        serverButton.addActionListener(e -> {
            dispose();
            ServerGame game = new ServerGame();
            game.setLoggedInUser(loggedInUser);
            game.setGameMode("online");
        });

        // Кнопка "Гра як клієнт"
        JButton clientButton = createStyledButton("Gra jako klient");
        gbc.gridy = 3;
        panel.add(clientButton, gbc);
        clientButton.addActionListener(e -> {
            dispose();
            String ip = JOptionPane.showInputDialog("Podaj IP serwera:");
            ClientGame game = new ClientGame(ip);
            game.setLoggedInUser(loggedInUser);
            game.setGameMode("online");
        });

        // Кнопка "Zaloguj się"
        JButton loginButton = createStyledButton("Zaloguj się");
        gbc.gridy = 4;
        panel.add(loginButton, gbc);
        loginButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(this, "Podaj nazwę użytkownika:");
            String password = JOptionPane.showInputDialog(this, "Podaj hasło:");
            if (DatabaseManager.authenticateUser(username, password)) {
                loggedInUser = username;
                JOptionPane.showMessageDialog(this, "Zalogowano jako: " + username);
            } else {
                JOptionPane.showMessageDialog(this, "Nieprawidłowy login lub hasło!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Кнопка "Wyloguj się"
        JButton logoutButton = createStyledButton("Wyloguj się");
        gbc.gridy = 5;
        panel.add(logoutButton, gbc);
        logoutButton.addActionListener(e -> {
            if (loggedInUser != null) {
                JOptionPane.showMessageDialog(this, "Wylogowano użytkownika: " + loggedInUser);
                loggedInUser = null;
            } else {
                JOptionPane.showMessageDialog(this, "Brak zalogowanego użytkownika!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }


    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(240, 240, 240));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setOpaque(true);
        return button;
    }

    private void addLoginLogoutButtons(JPanel panel) {
        JButton loginButton = createStyledButton("Zaloguj się");
        JButton logoutButton = createStyledButton("Wyloguj się");

        loginButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(this, "Podaj nazwę użytkownika:");
            String password = JOptionPane.showInputDialog(this, "Podaj hasło:");

            if (DatabaseManager.authenticateUser(username, password)) {
                JOptionPane.showMessageDialog(this, "Zalogowano jako: " + username);
                String stats = DatabaseManager.getPlayerStatistics(username);
                JOptionPane.showMessageDialog(this, stats, "Twoje statystyki", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Nieprawidłowy login lub hasło!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        logoutButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Wylogowano!"));

        panel.add(loginButton);
        panel.add(logoutButton);
    }

}
