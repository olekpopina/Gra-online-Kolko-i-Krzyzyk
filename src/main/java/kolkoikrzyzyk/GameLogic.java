package kolkoikrzyzyk;

/**
 * @brief Klasa zawierająca logikę gry dla "Kółko i Krzyżyk".
 *
 * @details Klasa implementuje metody pozwalające na sprawdzenie wyniku gry,
 * w tym określenie zwycięzcy oraz analizę linii na planszy.
 */
public class GameLogic {

    /**
     * @brief Określa zwycięzcę na podstawie aktualnego stanu gry.
     *
     * @param gameState Dwuwymiarowa tablica zawierająca stan gry.
     *        Każda komórka tablicy może zawierać "X", "O" lub null,
     *        w zależności od stanu pola na planszy.
     *
     * @return Symbol zwycięzcy ("X" lub "O") jeśli istnieje, lub null, jeśli nie ma zwycięzcy.
     *
     * @details Metoda sprawdza wszystkie rzędy, kolumny i przekątne w poszukiwaniu zwycięskiej linii.
     * Zwycięska linia to taka, gdzie wszystkie pola mają ten sam symbol ("X" lub "O").
     */
    public static String getWinner(String[][] gameState) {
        String winner;

        // Sprawdzanie rzędów
        for (int i = 0; i < 3; i++) {
            winner = checkLine(gameState[i][0], gameState[i][1], gameState[i][2]);
            if (winner != null) return winner;
        }

        // Sprawdzanie kolumn
        for (int i = 0; i < 3; i++) {
            winner = checkLine(gameState[0][i], gameState[1][i], gameState[2][i]);
            if (winner != null) return winner;
        }

        // Sprawdzanie przekątnych
        winner = checkLine(gameState[0][0], gameState[1][1], gameState[2][2]);
        if (winner != null) return winner;

        winner = checkLine(gameState[0][2], gameState[1][1], gameState[2][0]);
        return winner; // Może być null, jeśli nie ma zwycięzcy
    }

    /**
     * @brief Sprawdza, czy trzy pola tworzą zwycięską linię.
     *
     * @param a Pierwsze pole linii.
     * @param b Drugie pole linii.
     * @param c Trzecie pole linii.
     *
     * @return Symbol zwycięzcy ("X" lub "O") jeśli wszystkie pola są takie same, lub null, jeśli linia nie jest zwycięska.
     *
     * @details Aby linia była zwycięska, wszystkie trzy pola muszą zawierać ten sam symbol
     * ("X" lub "O") i żadne z pól nie może być null.
     */
    private static String checkLine(String a, String b, String c) {
        if (a != null && a.equals(b) && a.equals(c)) {
            return a; // Zwraca symbol zwycięzcy ("X" lub "O")
        }
        return null; // Brak zwycięzcy
    }
}
