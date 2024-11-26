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
        buttons[row][col].setIcon(getPlayerIcon(currentPlayer));
        turaGraczaX = !turaGraczaX;

        checkGameStatus();
    }
}
