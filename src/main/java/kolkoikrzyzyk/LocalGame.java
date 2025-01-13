package kolkoikrzyzyk;

/**
 * @brief Klasa reprezentująca tryb gry "Gra na jednym komputerze".
 *
 * @details Klasa rozszerza {@link GameBase} i implementuje logikę gry dla trybu lokalnego,
 * gdzie dwóch graczy gra na tym samym komputerze. Zawiera metody do wykonywania ruchów oraz zmiany tur.
 */
public class LocalGame extends GameBase {
    private boolean turaGraczaX = true; ///< Flaga określająca, która strona (X lub O) ma teraz turę.

    /**
     * @brief Konstruktor klasy LocalGame.
     *
     * @details Konstruktor ustawia tytuł okna na "Gra na jednym komputerze"
     * oraz wywołuje konstruktor klasy bazowej {@link GameBase}.
     */
    public LocalGame() {
        super("Gra na jednym komputerze");
    }

    /**
     * @brief Wykonuje ruch gracza w danym polu planszy.
     *
     * @param row Indeks wiersza, w którym gracz chce wykonać ruch.
     * @param col Indeks kolumny, w której gracz chce wykonać ruch.
     *
     * @details Metoda sprawdza, czy pole jest już zajęte. Jeśli nie, wykonuje ruch
     * gracza, przypisując odpowiedni symbol ("X" lub "O") do tablicy stanu gry.
     * Następnie ustawia ikonę na odpowiednim przycisku i przełącza turę na drugiego gracza.
     * Na końcu sprawdza stan gry, czy ktoś wygrał lub czy gra zakończyła się remisem.
     */
    @Override
    public void makeMove(int row, int col) {
        if (gameState[row][col] != null) return; // Sprawdza, czy pole jest już zajęte

        String currentPlayer = turaGraczaX ? "X" : "O"; // Określa, który gracz ma turę
        gameState[row][col] = currentPlayer; // Przypisuje symbol do pola na planszy

        // Ustawia ikonę na odpowiednim przycisku
        int buttonSize = buttons[row][col].getWidth();
        buttons[row][col].setIcon(getPlayerIcon(currentPlayer, buttonSize));
        buttons[row][col].setEnabled(false); // Dezaktywuje przycisk po wykonaniu ruchu
        buttons[row][col].setText(""); // Czyści tekst przycisku

        turaGraczaX = !turaGraczaX; // Przełącza turę na drugiego gracza
        printGameState(); // Drukuje stan gry w konsoli
        checkGameStatus(); // Sprawdza status gry
    }
}
