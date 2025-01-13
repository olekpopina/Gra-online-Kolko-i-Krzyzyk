package kolkoikrzyzyk;

/**
 * @brief Interfejs definiujący tryb gry w "Kółko i Krzyżyk".
 *
 * @details Interfejs ten określa podstawowe metody, które muszą być zaimplementowane
 * przez każdą klasę obsługującą różne tryby gry, takie jak gra lokalna, gra przeciwko botowi czy gra online.
 */
public interface GameMode {

    /**
     * @brief Wykonuje ruch gracza w określonej pozycji na planszy.
     *
     * @param row Wiersz planszy (0-2).
     * @param col Kolumna planszy (0-2).
     *
     * @details Metoda realizuje logikę wykonania ruchu w wybranym polu planszy.
     * W zależności od implementacji może uwzględniać lokalną grę, boty lub tryb online.
     */
    void makeMove(int row, int col);

    /**
     * @brief Resetuje stan gry do początkowego.
     *
     * @details Przywraca planszę do stanu początkowego, usuwając wszystkie symbole
     * oraz resetując wewnętrzny stan gry. Może być używane po zakończeniu gry, aby rozpocząć nową rozgrywkę.
     */
    void resetGame();
}
