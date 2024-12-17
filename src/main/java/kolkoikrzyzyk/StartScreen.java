package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class StartScreen extends JFrame {
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

        // Список найкращих гравців (хардкод)
        List<String> topPlayers = Arrays.asList("1. GraczX - 10 pkt", "2. GraczO - 8 pkt", "3. GraczZ - 6 pkt");
        for (String player : topPlayers) {
            JLabel playerLabel = new JLabel(player);
            playerLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            playerLabel.setForeground(Color.YELLOW);
            playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(playerLabel);
        }

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Перший рядок кнопок (2x3)
        JButton localGameButton = createStyledButton("Gra na jednym komputerze");
        JButton vsComputerButton = createStyledButton("Gra przeciw komputerowi");
        JButton serverButton = createStyledButton("Gra jako serwer");
        JButton clientButton = createStyledButton("Gra jako klient");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(localGameButton, gbc);

        gbc.gridx = 1;
        panel.add(vsComputerButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(serverButton, gbc);

        gbc.gridx = 1;
        panel.add(clientButton, gbc);

        // Широка кнопка для входу в акаунт
        JButton loginButton = createStyledButton("Zaloguj się");
        JButton logoutButton = createStyledButton("Wyloguj się");

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        gbc.gridy = 3;
        panel.add(logoutButton, gbc);

        // Обробники подій
        localGameButton.addActionListener(e -> {
            dispose();
            new LocalGame();
        });

        vsComputerButton.addActionListener(e -> {
            dispose();
            new VsComputerGame();
        });

        serverButton.addActionListener(e -> {
            dispose();
            new ServerGame();
        });

        clientButton.addActionListener(e -> {
            dispose();
            String ip = JOptionPane.showInputDialog("Podaj IP serwera:");
            new ClientGame(ip);
        });

        loginButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Zalogowano!"));
        logoutButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Wylogowano!"));

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
}
