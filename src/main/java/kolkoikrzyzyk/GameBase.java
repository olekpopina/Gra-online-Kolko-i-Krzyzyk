package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

public abstract class GameBase extends JFrame implements GameMode {
    protected final JButton[][] buttons = new JButton[3][3];
    protected final String[][] gameState = new String[3][3];
    protected BufferedImage obrazekX = ResourceLoader.loadImage("/images/x.png");
    protected BufferedImage obrazekO = ResourceLoader.loadImage("/images/o.png");
    private String loggedInUser = null;
    protected String playerSymbol = "X";
    private String gameMode = "local"; // Можливо значення: "local", "vs_bot", "online"

    public GameBase(String title) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Початковий розмір вікна
        int windowSize = 600;
        setSize(windowSize, windowSize);
        setMinimumSize(new Dimension(400, 400)); // Мінімальний розмір вікна

        BackgroundPanel backgroundPanel = new BackgroundPanel("/images/tlo.png");
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(3, 3, 0, 20));
        gamePanel.setOpaque(false);

        initializeButtons(gamePanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 20, 20, 20); // Відступи навколо сітки
        backgroundPanel.add(gamePanel, gbc);

        // Встановлення стартового розміру сітки кнопок
        int gridSize = (int) (windowSize * 0.65); // 65% від розміру вікна
        gamePanel.setPreferredSize(new Dimension(gridSize, gridSize));
        updateButtonSizes(gridSize / 3);


        addEscKeyListener();

        setVisible(true);

        // Додаємо слухача для динамічної зміни розміру
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setGridSize(gamePanel);
            }
        });
    }

    public GameBase(String title, String gameMode, String playerSymbol) {
        this(title);
        this.gameMode = gameMode;
        this.playerSymbol = playerSymbol;
    }

    public void setLoggedInUser(String username) {
        this.loggedInUser = username;
    }

    private void initializeButtons(JPanel panel) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setOpaque(false);
                buttons[i][j].setContentAreaFilled(false);
                buttons[i][j].setBorderPainted(false);
                buttons[i][j].setEnabled(true); // Всі кнопки мають бути активними
                buttons[i][j].setText(""); // Очищаємо текст кнопки
                buttons[i][j].setIcon(null); // Без значка
                gameState[i][j] = null; // Ігровий стан клітинки пустий

                int finalI = i;
                int finalJ = j;
                buttons[i][j].addActionListener(e -> makeMove(finalI, finalJ));
                panel.add(buttons[i][j]);
            }
        }
    }

    private void setGridSize(JPanel gamePanel) {
        int windowWidth = getWidth();
        int windowHeight = getHeight();

        // Обмежуємо розмір сітки (50% від поточного розміру вікна)
        int gridSize = (int) (Math.min(windowWidth, windowHeight) * 0.65);
        gamePanel.setPreferredSize(new Dimension(gridSize, gridSize));

        updateButtonSizes(gridSize / 3);
        revalidate();
        repaint();
    }


    private void updateButtonSizes(int buttonSize) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setPreferredSize(new Dimension(buttonSize, buttonSize));
                String player = gameState[i][j];
                if (player != null) {
                    buttons[i][j].setIcon(getPlayerIcon(player, buttonSize)); // Оновлюємо іконку
                }
            }
        }
    }

    public void updateWindowTitle() {
        if (loggedInUser != null) {
            setTitle(getTitle() + " - Zalogowano jako: " + loggedInUser);
        }
    }



    protected ImageIcon getPlayerIcon(String player, int buttonSize) {
        if (player == null || player.isEmpty()) return null;

        BufferedImage image = "X".equals(player) ? obrazekX : obrazekO;
        if (image == null) return null;

        int scaledSize = (int) (buttonSize * 0.8); // 80% від розміру кнопки
        Image scaledImage = image.getScaledInstance(scaledSize, scaledSize, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }



    @Override
    public void resetGame() {
        if (loggedInUser != null) {
            String winner = GameLogic.getWinner(gameState);
            boolean isDraw = isBoardFull() && winner == null;

            if (!isDraw) { // Тільки якщо це не нічия
                boolean win = Objects.equals(winner, playerSymbol); // Перевірка, чи переміг поточний гравець
                DatabaseManager.updateUserStats(loggedInUser, win, gameMode);
            }
        }

        // Очищення гри
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setIcon(null);
                buttons[i][j].setEnabled(true);
                gameState[i][j] = null;
            }
        }

        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
        });
    }

    protected boolean checkGameStatus() {
        String winner = GameLogic.getWinner(gameState);
        boolean isDraw = isBoardFull() && winner == null;

        if (winner != null) { // Якщо є переможець
            JOptionPane.showMessageDialog(this, "Wygrywa gracz: " + winner + "!", "Wynik gry", JOptionPane.INFORMATION_MESSAGE);
        } else if (isDraw) { // Якщо нічия
            JOptionPane.showMessageDialog(this, "Remis!", "Wynik gry", JOptionPane.INFORMATION_MESSAGE);
        } else {
            return false; // Гра ще не закінчена
        }

        resetGame(); // Завершення гри і запис результатів
        return true;
    }


    private boolean isBoardFull() {
        for (String[] row : gameState) {
            for (String cell : row) {
                if (cell == null) return false;
            }
        }
        return true;
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Błąd", JOptionPane.ERROR_MESSAGE);
    }

    void printGameState() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(gameState[i][j] == null ? "." : gameState[i][j]);
            }
            System.out.println();
        }
    }

    protected void returnToMainMenu() {
        int option = JOptionPane.showConfirmDialog(this, "Czy na pewno chcesz wrócić do głównego menu?",
                "Powrót do menu", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            dispose(); // Закриваємо поточне вікно
            new StartScreen(loggedInUser); // Відкриваємо головне меню
        }
    }

    private void addEscKeyListener() {
        JRootPane rootPane = this.getRootPane();

        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "returnToMainMenu");
        actionMap.put("returnToMainMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToMainMenu();
            }
        });
    }

}
