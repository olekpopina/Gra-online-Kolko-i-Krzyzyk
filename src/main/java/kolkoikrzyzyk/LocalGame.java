package kolkoikrzyzyk;


public class LocalGame extends GameBase {
    private boolean turaGraczaX = true;

    public LocalGame() {
        super("Gra na jednym komputerze");
    }

    @Override
    public void makeMove(int row, int col) {
        if (gameState[row][col] != null) return;

        String currentPlayer = turaGraczaX ? "X" : "O";
        gameState[row][col] = currentPlayer;

        // Встановлюємо лише іконку
        buttons[row][col].setIcon(getPlayerIcon(currentPlayer));
        buttons[row][col].setEnabled(false);
        buttons[row][col].setText(""); // Очищаємо текст

        turaGraczaX = !turaGraczaX;
        printGameState();
        checkGameStatus();
    }


}
