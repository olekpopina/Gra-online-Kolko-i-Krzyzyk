package kolkoikrzyzyk;


public class LocalGame extends GameBase {
    private boolean turaGraczaX = true;

    public LocalGame() {
        super("Gra na jednym komputerze");
    }

    @Override
    public void makeMove(int row, int col) {
        if (gameState[row][col] != null) return; // Пропускаємо, якщо клітинка зайнята

        String currentPlayer = turaGraczaX ? "X" : "O";
        gameState[row][col] = currentPlayer; // Оновлюємо стан гри
        buttons[row][col].setText(currentPlayer); // Встановлюємо текст
        buttons[row][col].setIcon(getPlayerIcon(currentPlayer)); // Встановлюємо значок
        buttons[row][col].setEnabled(false); // Забороняємо повторний клік
        turaGraczaX = !turaGraczaX; // Змінюємо черговість ходу
        printGameState();
        checkGameStatus(); // Перевіряємо стан гри
    }

}
