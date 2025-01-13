package kolkoikrzyzyk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @brief Reprezentuje grę "Kółko i Krzyżyk" przeciwko komputerowi.
 *
 * @details Klasa dziedziczy z GameBase i implementuje logikę gry, w której gracz
 * gra symbolem "X", a komputer symbolem "O". Komputer wykonuje ruchy losowo.
 */
public class VsComputerGame extends GameBase {

    /**
     * @brief Konstruktor klasy VsComputerGame.
     *
     * @details Ustawia tytuł okna gry, tryb gry ("vs_bot"),
     * oraz symbol gracza ("X").
     */
    public VsComputerGame() {
        super("Gra przeciw komputerowi ( X )", "vs_bot", "X");
    }

    /**
     * @brief Obsługuje ruch gracza w wybranym polu.
     *
     * @param row Wiersz planszy (0-2).
     * @param col Kolumna planszy (0-2).
     *
     * @details Jeśli pole jest puste, wstawia symbol gracza ("X"),
     * aktualizuje stan gry i sprawdza, czy gra się zakończyła.
     * Następnie komputer wykonuje swój ruch.
     */
    @Override
    public void makeMove(int row, int col) {
        if (gameState[row][col] != null) return; // Sprawdzenie, czy pole jest już zajęte

        gameState[row][col] = "X"; // Gracz zawsze gra "X"
        int buttonSize = buttons[row][col].getWidth();
        buttons[row][col].setIcon(getPlayerIcon("X", buttonSize));

        if (checkGameStatus()) return; // Sprawdzanie, czy gra się zakończyła

        makeComputerMove(); // Ruch komputera
    }

    /**
     * @brief Wykonuje ruch komputera.
     *
     * @details Komputer losowo wybiera jedno z dostępnych pól na planszy.
     * Wstawia symbol komputera ("O") w wybranym polu, aktualizuje stan gry
     * i sprawdza, czy gra się zakończyła.
     */
    private void makeComputerMove() {
        List<int[]> availableMoves = new ArrayList<>();

        // Szukanie pustych pól na planszy
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameState[i][j] == null) {
                    availableMoves.add(new int[]{i, j});
                }
            }
        }

        if (!availableMoves.isEmpty()) {
            // Losowy wybór jednego z dostępnych ruchów
            int[] move = availableMoves.get(new Random().nextInt(availableMoves.size()));
            gameState[move[0]][move[1]] = "O"; // Komputer gra "O"

            // Pobieranie dynamicznego rozmiaru przycisku
            int buttonSize = buttons[move[0]][move[1]].getWidth();
            if (buttonSize == 0) { // Jeśli przycisk nie jest jeszcze widoczny
                buttonSize = 100; // Domyślny rozmiar
            }

            buttons[move[0]][move[1]].setIcon(getPlayerIcon("O", buttonSize));
            checkGameStatus(); // Sprawdzanie, czy gra się zakończyła
        }
    }
}
