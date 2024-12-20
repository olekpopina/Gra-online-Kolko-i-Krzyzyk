package kolkoikrzyzyk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VsComputerGame extends GameBase {
    public VsComputerGame() {
        super("Gra przeciw komputerowi");
    }

    @Override
    public void makeMove(int row, int col) {
        if (gameState[row][col] != null) return;

        gameState[row][col] = "X"; // Гравець завжди "X"
        int buttonSize = buttons[row][col].getWidth();
        buttons[row][col].setIcon(getPlayerIcon("X", buttonSize));

        if (checkGameStatus()) return;

        makeComputerMove();
    }

    private void makeComputerMove() {
        List<int[]> availableMoves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameState[i][j] == null) {
                    availableMoves.add(new int[]{i, j});
                }
            }
        }

        if (!availableMoves.isEmpty()) {
            int[] move = availableMoves.get(new Random().nextInt(availableMoves.size()));
            gameState[move[0]][move[1]] = "O"; // Комп'ютер завжди "O"

            // Отримуємо розмір кнопки динамічно
            int buttonSize = buttons[move[0]][move[1]].getWidth();
            if (buttonSize == 0) { // Якщо кнопка ще не відображена
                buttonSize = 100; // Значення за замовчуванням
            }

            buttons[move[0]][move[1]].setIcon(getPlayerIcon("O", buttonSize));
            checkGameStatus();
        }
    }
}
