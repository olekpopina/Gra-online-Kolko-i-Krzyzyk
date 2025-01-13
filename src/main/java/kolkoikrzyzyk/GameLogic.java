package kolkoikrzyzyk;

public class GameLogic {

    // Перевірка на перемогу або визначення переможця
    public static String getWinner(String[][] gameState) {
        String winner;

        // Перевірка рядків
        for (int i = 0; i < 3; i++) {
            winner = checkLine(gameState[i][0], gameState[i][1], gameState[i][2]);
            if (winner != null) return winner;
        }

        // Перевірка стовпців
        for (int i = 0; i < 3; i++) {
            winner = checkLine(gameState[0][i], gameState[1][i], gameState[2][i]);
            if (winner != null) return winner;
        }

        // Перевірка діагоналей
        winner = checkLine(gameState[0][0], gameState[1][1], gameState[2][2]);
        if (winner != null) return winner;

        winner = checkLine(gameState[0][2], gameState[1][1], gameState[2][0]);
        return winner; // Може бути null, якщо переможця немає
    }

    // Перевірка, чи лінія має однакові символи
    private static String checkLine(String a, String b, String c) {
        if (a != null && a.equals(b) && a.equals(c)) {
            return a; // Повертаємо символ переможця ("X" або "O")
        }
        return null; // Немає переможця
    }

    // Перевіряє, чи переміг конкретний гравець
    public static boolean isPlayerWinner(String[][] gameState, String playerSymbol) {
        return playerSymbol.equals(getWinner(gameState));
    }
}
