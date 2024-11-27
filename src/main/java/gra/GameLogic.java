package gra;

public class GameLogic {

    public static boolean checkWin(String[][] gameState) {
        for (int i = 0; i < 3; i++) {

            if (checkLine(gameState[i][0], gameState[i][1], gameState[i][2]) ||
                    checkLine(gameState[0][i], gameState[1][i], gameState[2][i])) {
                return true;
            }
        }

        return checkLine(gameState[0][0], gameState[1][1], gameState[2][2]) ||
                checkLine(gameState[0][2], gameState[1][1], gameState[2][0]);
    }

    private static boolean checkLine(String a, String b, String c) {
        return a != null && a.equals(b) && b.equals(c);
    }
}
