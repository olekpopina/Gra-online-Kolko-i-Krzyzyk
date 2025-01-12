package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

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

    public void setGameMode(String mode) {
        this.gameMode = mode;
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
            boolean win;
            if (gameMode != "local"){
                win = GameLogic.checkWin(gameState, playerSymbol);
            }
            else  {
                win = GameLogic.checkWin(gameState);
            }

            boolean draw = isBoardFull() && !win; // Перевірка на нічию

            if (!draw) { // Записуємо тільки якщо це не нічия
                DatabaseManager.updateUserStats(loggedInUser, win, gameMode);
            }
        }

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
        String winner = getWinner(); // Отримуємо символ переможця

        if (winner != null) { // Якщо є переможець
            JOptionPane.showMessageDialog(this, "Wygrywa gracz: " + winner + "!", "Wynik gry", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
            return true;
        } else if (isBoardFull()) { // Нічия
            JOptionPane.showMessageDialog(this, "Remis!", "Wynik gry", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
            return true;
        }
        return false;
    }

    protected void checkAndSaveGameResult() {
        String winner = getWinner(); // Локальне визначення переможця
        boolean isDraw = isBoardFull() && winner == null;

        if (winner != null || isDraw) {
            String message = isDraw ? "Remis!" : "Wygrywa gracz: " + winner;
            JOptionPane.showMessageDialog(this, message);

//            if (loggedInUser != null) {
//                boolean win = loggedInUser.equals(winner); // Перевірка, чи залогований гравець виграв
//                DatabaseManager.updateUserStats(loggedInUser, win, gameMode);
//            }

            resetGame();
        }
    }


    protected String getWinner() {
        for (int i = 0; i < 3; i++) {
            // Перевірка рядків
            if (gameState[i][0] != null && gameState[i][0].equals(gameState[i][1]) && gameState[i][0].equals(gameState[i][2])) {
                return gameState[i][0];
            }
            // Перевірка стовпців
            if (gameState[0][i] != null && gameState[0][i].equals(gameState[1][i]) && gameState[0][i].equals(gameState[2][i])) {
                return gameState[0][i];
            }
        }
        // Перевірка діагоналей
        if (gameState[0][0] != null && gameState[0][0].equals(gameState[1][1]) && gameState[0][0].equals(gameState[2][2])) {
            return gameState[0][0];
        }
        if (gameState[0][2] != null && gameState[0][2].equals(gameState[1][1]) && gameState[0][2].equals(gameState[2][0])) {
            return gameState[0][2];
        }
        return null; // Немає переможця
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
