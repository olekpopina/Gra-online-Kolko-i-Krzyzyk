import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private boolean isXTurn = true;

    public Main() {
        setTitle("Kółko i Krzyżyk");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 3));

        initializeButtons();
    }

    private void initializeButtons() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col] = new JButton("");
                buttons[row][col].setFont(new Font("Arial", Font.PLAIN, 60));
                buttons[row][col].addActionListener(new ButtonClickListener(row, col));
                add(buttons[row][col]);
            }
        }
    }

    private class ButtonClickListener implements ActionListener {
        private int row, col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!buttons[row][col].getText().equals("")) return;

            buttons[row][col].setText(isXTurn ? "X" : "O");
            isXTurn = !isXTurn;

            // Sprawdzenie zwycięzcy po każdym ruchu
            String winner = checkWinner();
            if (winner != null) {
                JOptionPane.showMessageDialog(Main.this, "Zwycięstwo " + winner);
                resetBoard();
            }
        }
    }

    // Metoda sprawdzająca zwycięzcę
    private String checkWinner() {
        // Sprawdzenie wierszy
        for (int row = 0; row < 3; row++) {
            if (buttons[row][0].getText().equals(buttons[row][1].getText()) &&
                    buttons[row][1].getText().equals(buttons[row][2].getText()) &&
                    !buttons[row][0].getText().equals("")) {
                return buttons[row][0].getText();
            }
        }

        // Sprawdzenie kolumn
        for (int col = 0; col < 3; col++) {
            if (buttons[0][col].getText().equals(buttons[1][col].getText()) &&
                    buttons[1][col].getText().equals(buttons[2][col].getText()) &&
                    !buttons[0][col].getText().equals("")) {
                return buttons[0][col].getText();
            }
        }

        // Sprawdzenie przekątnych
        if (buttons[0][0].getText().equals(buttons[1][1].getText()) &&
                buttons[1][1].getText().equals(buttons[2][2].getText()) &&
                !buttons[0][0].getText().equals("")) {
            return buttons[0][0].getText();
        }

        if (buttons[0][2].getText().equals(buttons[1][1].getText()) &&
                buttons[1][1].getText().equals(buttons[2][0].getText()) &&
                !buttons[0][2].getText().equals("")) {
            return buttons[0][2].getText();
        }

        // Brak zwycięzcy
        return null;
    }

    // Metoda resetująca planszę po wygranej
    private void resetBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col].setText("");
            }
        }
        isXTurn = true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}