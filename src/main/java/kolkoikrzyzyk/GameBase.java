package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

public abstract class GameBase extends JFrame implements GameMode {
    protected final JButton[][] buttons = new JButton[3][3];
    protected final String[][] gameState = new String[3][3];
    protected BufferedImage obrazekX = ResourceLoader.loadImage("/images/x.png");
    protected BufferedImage obrazekO = ResourceLoader.loadImage("/images/o.png");

    public GameBase(String title) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);


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
        backgroundPanel.add(gamePanel, gbc);

        setVisible(true);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateButtonSizes();
            }
        });
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
                buttons[i][j].setText("V"); // Початково кнопка порожня
                buttons[i][j].setIcon(null); // Без значка
                gameState[i][j] = null; // Ігровий стан клітинки пустий

                int finalI = i;
                int finalJ = j;
                buttons[i][j].addActionListener(e -> makeMove(finalI, finalJ));
                panel.add(buttons[i][j]);
            }
        }
    }



    private void updateButtonSizes() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String player = buttons[i][j].getText();
                if (!player.isEmpty()) { // Оновлюємо значок, якщо є текст
                    buttons[i][j].setIcon(getPlayerIcon(player));
                } else {
                    buttons[i][j].setIcon(null); // Прибираємо значок, якщо текст порожній
                }
            }
        }
    }


    protected ImageIcon getPlayerIcon(String player) {
        if (player == null || player.isEmpty()) return null;

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int buttonWidth = panelWidth / 5;
        int buttonHeight = panelHeight / 5;

        BufferedImage image = "X".equals(player) ? obrazekX : obrazekO;
        if (image == null) return null;

        int scaledWidth = (int) (buttonWidth * 0.8);  // 80% від ширини кнопки
        int scaledHeight = (int) (buttonHeight * 0.8); // 80% від висоти кнопки
        Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }


    @Override
    public void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("P"); // Очищаємо текст кнопок
                buttons[i][j].setIcon(null); // Видаляємо значки
                buttons[i][j].setEnabled(true); // Робимо кнопки активними
                gameState[i][j] = null; // Скидаємо стан гри
            }
        }
    }

    protected boolean checkGameStatus() {
        if (GameLogic.checkWin(gameState)) {
            JOptionPane.showMessageDialog(this, "Wygrywa!");
            resetGame();
            return true;
        } else if (isBoardFull()) {
            JOptionPane.showMessageDialog(this, "Remis!");
            resetGame();
            return true;
        }
        return false;
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

}
