package kolkoikrzyzyk;

import javax.swing.*;
import java.awt.*;
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
        backgroundPanel.setLayout(new GridLayout(3, 3));
        setContentPane(backgroundPanel);

        initializeButtons(backgroundPanel);
        setVisible(true);
    }

    private void initializeButtons(JPanel panel) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setOpaque(false);
                buttons[i][j].setContentAreaFilled(false);
                buttons[i][j].setBorderPainted(false);
                int finalI = i;
                int finalJ = j;
                buttons[i][j].addActionListener(e -> makeMove(finalI, finalJ));
                panel.add(buttons[i][j]);
            }
        }
//        updateButtonSizes();
    }

//    private void updateButtonSizes() {
//        int panelWidth = getWidth();
//        int panelHeight = getHeight();
//
//        int buttonWidth = panelWidth / 5; // Пропорційна ширина
//        int buttonHeight = panelHeight / 5; // Пропорційна висота
//
//        int startX = (panelWidth - buttonWidth * 3) / 2; // Центруємо по горизонталі
//        int startY = (panelHeight - buttonHeight * 3) / 2; // Центруємо по вертикалі
//
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                int x = startX + j * buttonWidth;
//                int y = startY + i * buttonHeight;
//                buttons[i][j].setBounds(x, y, buttonWidth, buttonHeight);
//
//                int finalI = i;
//                int finalJ = j;
//                SwingUtilities.invokeLater(() -> updateButtonIcon(buttons[finalI][finalJ], buttonWidth, buttonHeight));
//            }
//        }
//    }

    @Override
    public void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setIcon(null);
                gameState[i][j] = null;
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

    protected ImageIcon getPlayerIcon(String player) {
        BufferedImage image = player.equals("X") ? obrazekX : obrazekO;
        return new ImageIcon(image.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Błąd", JOptionPane.ERROR_MESSAGE);
    }
}
