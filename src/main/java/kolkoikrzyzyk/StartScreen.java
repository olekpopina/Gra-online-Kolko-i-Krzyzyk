package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;


public class StartScreen extends JFrame {
    private String loggedInUser; // Збереження імені залогіненого користувача

    public StartScreen(String username) {
        this.loggedInUser = username; // Ім'я залогіненого користувача
        setTitle("Kółko i Krzyżyk - Zalogowano jako: " + (username != null ? username : "Gość"));
        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel("/images/tlo.png");
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Верхня панель: логін користувача + топ-10 гравців
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        // Логін користувача (вирівняний вправо)
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        JLabel userLabel = new JLabel("Zalogowano jako: " + (loggedInUser != null ? loggedInUser : "Gość"));
        userLabel.setFont(new Font("Arial", Font.BOLD, 18));
        userLabel.setForeground(Color.WHITE);
        userPanel.add(userLabel);

        // Топ-10 гравців
        JPanel topPlayersPanel = createTopPlayersPanel();

        // Додаємо логін і топ-10 в єдину верхню панель
        topPanel.add(userPanel);
        topPanel.add(topPlayersPanel);

        backgroundPanel.add(topPanel, BorderLayout.NORTH);

        // Середня панель з кнопками
        JPanel buttonPanel = createButtonPanel();
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createTopPlayersPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Najlepsi gracze:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        List<String> topPlayers = DatabaseManager.getTopPlayers(); // Отримання топ-10 гравців
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
        gbc.insets = new Insets(5, 10, 5, 10); // Відступи між кнопками
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Кнопки з режимами гри
        JButton localGameButton = createStyledButton("Gra na jednym komputerze");
        gbc.gridy = 0;
        panel.add(localGameButton, gbc);
        localGameButton.addActionListener(e -> {
            dispose();
            LocalGame game = new LocalGame();
            game.setLoggedInUser(loggedInUser);
            game.updateWindowTitle();
        });

        JButton vsComputerButton = createStyledButton("Gra przeciw komputerowi");
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
            dispose(); // Закриваємо головне меню
            new ServerGame(); // Запускаємо сервер гри
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

        // Кнопка залогінитися
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

        // Кнопка розлогінитися
        JButton logoutButton = createStyledButton("Wyloguj się");
        gbc.gridy = 5;
        panel.add(logoutButton, gbc);
        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Wylogowano!");
            dispose();
            new StartScreen(null);
        });

        // Кнопка виходу
        JButton exitButton = createStyledButton("Wyjście");
        gbc.gridy = 6;
        panel.add(exitButton, gbc);
        exitButton.addActionListener(e -> System.exit(0));

        return panel;
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
}
