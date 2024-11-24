package gra;

import javax.swing.*;

public class GameLogic {
    public static boolean checkWin(JButton[][] buttons) {
        for (int i = 0; i < 3; i++) {
            // Перевірка рядків і стовпців
            if (checkLine(buttons[i][0], buttons[i][1], buttons[i][2]) ||
                    checkLine(buttons[0][i], buttons[1][i], buttons[2][i])) {
                return true;
            }
        }
        // Перевірка діагоналей
        return checkLine(buttons[0][0], buttons[1][1], buttons[2][2]) ||
                checkLine(buttons[0][2], buttons[1][1], buttons[2][0]);
    }

    private static boolean checkLine(JButton b1, JButton b2, JButton b3) {
        return b1.getIcon() != null &&
                b1.getIcon().equals(b2.getIcon()) &&
                b2.getIcon().equals(b3.getIcon());
    }
}
