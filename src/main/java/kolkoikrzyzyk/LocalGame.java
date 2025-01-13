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
        int buttonSize = buttons[row][col].getWidth();
        buttons[row][col].setIcon(getPlayerIcon(currentPlayer, buttonSize));
        buttons[row][col].setEnabled(false);
        buttons[row][col].setText(""); // Очищаємо текст

        turaGraczaX = !turaGraczaX;
        printGameState();
        checkGameStatus();
    }


}
