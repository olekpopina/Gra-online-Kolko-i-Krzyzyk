package kolkoikrzyzyk;

public class GameLogic {
    // Перевірка загальної перемоги (без прив'язки до гравця)
    public static boolean checkWin(String[][] gameState) {
        return checkRowsAndColumns(gameState, null) || checkDiagonals(gameState, null);
    }

    // Перевірка перемоги для конкретного гравця
    public static boolean checkWin(String[][] gameState, String playerSymbol) {
        return checkRowsAndColumns(gameState, playerSymbol) || checkDiagonals(gameState, playerSymbol);
    }

    // Перевіряє рядки та стовпці
    private static boolean checkRowsAndColumns(String[][] gameState, String playerSymbol) {
        for (int i = 0; i < 3; i++) {
            // Перевірка рядка
            if (checkLine(gameState[i][0], gameState[i][1], gameState[i][2], playerSymbol)) {
                return true;
            }
            // Перевірка стовпця
            if (checkLine(gameState[0][i], gameState[1][i], gameState[2][i], playerSymbol)) {
                return true;
            }
        }
        return false;
    }

    // Перевіряє діагоналі
    private static boolean checkDiagonals(String[][] gameState, String playerSymbol) {
        // Головна діагональ
        if (checkLine(gameState[0][0], gameState[1][1], gameState[2][2], playerSymbol)) {
            return true;
        }
        // Побічна діагональ
        if (checkLine(gameState[0][2], gameState[1][1], gameState[2][0], playerSymbol)) {
            return true;
        }
        return false;
    }

    // Універсальна перевірка лінії
    private static boolean checkLine(String a, String b, String c, String playerSymbol) {
        if (playerSymbol == null) {
            // Перевірка загальної перемоги
            return a != null && a.equals(b) && b.equals(c);
        } else {
            // Перевірка для конкретного гравця
            return a != null && a.equals(playerSymbol) &&
                    b != null && b.equals(playerSymbol) &&
                    c != null && c.equals(playerSymbol);
        }
    }
}
