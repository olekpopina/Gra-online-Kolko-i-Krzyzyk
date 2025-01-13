package kolkoikrzyzyk;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Konstruktor klasy VsComputerGame.
 *
 * @details Ustawia tytuł okna gry, tryb gry ("vs_bot"),
 * oraz symbol gracza ("X").
 */
public class GameLogicTests {
    /**
     * @brief Test sprawdza, czy zwycięzca zostaje poprawnie wykryty w wierszach.
     *
     * @details W tej sytuacji gracz "X" wypełnia cały pierwszy wiersz.
     */
    @Test
    public void testGetWinnerForRows() {
        String[][] gameState = {
                {"X", "X", "X"},
                {null, null, null},
                {null, null, null}
        };
        assertEquals("X", GameLogic.getWinner(gameState));
    }
    /**
     * @brief Test sprawdza, czy zwycięzca zostaje poprawnie wykryty w kolumnach.
     *
     * @details W tej sytuacji gracz "O" wypełnia całą pierwszą kolumnę.
     */
    @Test
    public void testGetWinnerForColumns() {
        String[][] gameState = {
                {"O", null, null},
                {"O", null, null},
                {"O", null, null}
        };
        assertEquals("O", GameLogic.getWinner(gameState));
    }
    /**
     * @brief Test sprawdza, czy zwycięzca zostaje poprawnie wykryty na przekątnej.
     *
     * @details W tej sytuacji gracz "X" wypełnia przekątną od lewego górnego rogu
     * do prawego dolnego rogu.
     */
    @Test
    public void testGetWinnerForDiagonals() {
        String[][] gameState = {
                {"X", null, null},
                {null, "X", null},
                {null, null, "X"}
        };
        assertEquals("X", GameLogic.getWinner(gameState));
    }
    /**
     * @brief Test sprawdza, czy metoda poprawnie zwraca brak zwycięzcy w przypadku remisu.
     *
     * @details Plansza jest całkowicie wypełniona, ale żaden gracz nie wypełnia
     * pełnego wiersza, kolumny lub przekątnej.
     */
    @Test
    public void testNoWinnerDraw() {
        String[][] gameState = {
                {"X", "O", "X"},
                {"O", "X", "O"},
                {"O", "X", "O"}
        };
        assertNull(GameLogic.getWinner(gameState));
    }
    /**
     * @brief Test sprawdza, czy metoda poprawnie zwraca brak zwycięzcy na pustej planszy.
     *
     * @details Plansza nie zawiera żadnych ruchów.
     */
    @Test
    public void testNoWinnerEmptyBoard() {
        String[][] gameState = {
                {null, null, null},
                {null, null, null},
                {null, null, null}
        };
        assertNull(GameLogic.getWinner(gameState));
    }
    /**
     * @brief Test sprawdza poprawność obliczania stosunku wygranych do rozegranych gier.
     *
     * @details Tworzony jest obiekt UserStats z danymi o rozegranych, wygranych
     * i przegranych grach. Oczekiwany wynik stosunku to 0.6.
     */
    @Test
    public void testWinRatioCalculation() {
        UserStats stats = new UserStats(10, 6, 4, 2, 4);
        assertEquals(0.6, stats.getWinRatio(), 0.01);
    }
    /**
     * @brief Test sprawdza stosunek wygranych do rozegranych gier, gdy nie rozegrano żadnych gier.
     *
     * @details Oczekiwany stosunek to 0.0, ponieważ nie ma rozegranych gier.
     */
    @Test
    public void testNoGamesPlayedWinRatio() {
        UserStats stats = new UserStats(0, 0, 0, 0, 0);
        assertEquals(0.0, stats.getWinRatio(), 0.01);
    }
    /**
     * @brief Test rejestracji i uwierzytelniania użytkownika w bazie danych.
     *
     * @details Sprawdza, czy nowo zarejestrowany użytkownik może się uwierzytelnić.
     */
    @Test
    public void testDatabaseUserRegistration() {
        DatabaseManager.registerUser("TestUser", "password");
        assertTrue(DatabaseManager.authenticateUser("TestUser", "password"));

        DatabaseManager.deleteUser("TestUser");
    }
    /**
     * @brief Test aktualizacji statystyk użytkownika w bazie danych.
     *
     * @details Sprawdza, czy statystyki gracza są poprawnie aktualizowane po zakończeniu gry.
     */
    @Test
    public void testUpdateUserStats() {
        DatabaseManager.registerUser("TestPlayer", "password");
        DatabaseManager.updateUserStats("TestPlayer", true, "vs_bot");
        UserStats stats = DatabaseManager.getUserStats("TestPlayer");
        assertNotNull(stats);
        assertEquals(0, stats.getWins());
        assertEquals(0, stats.getGamesPlayed());
        assertEquals(1, stats.getGamesVsBot());

        DatabaseManager.deleteUser("TestPlayer");
    }
    /**
     * @brief Test wykonania ruchu przez gracza w grze przeciwko komputerowi.
     *
     * @details Sprawdza, czy ruch gracza zostaje poprawnie zarejestrowany w stanie gry.
     */
    @Test
    public void testVsComputerGameMove() {
        VsComputerGame game = new VsComputerGame();
        game.makeMove(0, 0);
        assertEquals("X", game.gameState[0][0]);
    }
    /**
     * @brief Test wykonania ruchu przez komputer w grze przeciwko graczowi.
     *
     * @details Sprawdza, czy komputer wykonuje swój ruch po ruchu gracza.
     */
    @Test
    public void testComputerMakesMove() {
        VsComputerGame game = new VsComputerGame();
        game.makeMove(0, 0);

        boolean computerMoved = false;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (game.gameState[i][j] != null && !game.gameState[i][j].equals("X")) {
                    computerMoved = true;
                    break;
                }
            }
        }
        assertTrue(computerMoved);
    }
    /**
     * @brief Test inicjalizacji statystyk użytkownika w bazie danych.
     *
     * @details Sprawdza, czy nowo zarejestrowany użytkownik ma początkowe wartości statystyk.
     */
    @Test
    public void testDatabaseUserStatsInitialization() {
        DatabaseManager.registerUser("StatTestUser", "password");
        UserStats stats = DatabaseManager.getUserStats("StatTestUser");
        assertNotNull(stats);
        assertEquals(0, stats.getGamesPlayed());
        assertEquals(0, stats.getWins());
        assertEquals(0, stats.getLosses());

        DatabaseManager.deleteUser("StatTestUser");
    }
    /**
     * @brief Test resetowania gry lokalnej.
     *
     * @details Sprawdza, czy wszystkie pola na planszy zostają wyczyszczone po resecie gry.
     */
    @Test
    public void testLocalGameReset() {
        LocalGame game = new LocalGame();
        game.gameState[0][0] = "X";
        game.resetGame();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertNull(game.gameState[i][j]);
            }
        }
    }
    /**
     * @brief Test połączenia serwera gry.
     *
     * @details Sprawdza, czy serwer gry może zostać uruchomiony bez wyjątków.
     */
    @Test
    public void testServerGameConnection() {
        ServerGame server = new ServerGame();
        assertDoesNotThrow(server::startServer);
    }
    /**
     * @brief Test połączenia klienta z serwerem gry.
     *
     * @details Sprawdza, czy klient może połączyć się z serwerem gry.
     */
    @Test
    public void testClientGameConnection() {
        ServerGame server = new ServerGame();
        server.startServer();
        assertDoesNotThrow(() -> {
            ClientGame client = new ClientGame("localhost");
            client.connectToServer("localhost");
        });
    }
    /**
     * @brief Test sprawdzający sytuację remisu w grze.
     *
     * @details Plansza jest całkowicie wypełniona, ale żaden gracz nie wygrywa.
     */
    @Test
    public void testDrawCondition() {
        String[][] gameState = {
                {"X", "O", "X"},
                {"O", "X", "O"},
                {"O", "X", "O"}
        };
        assertNull(GameLogic.getWinner(gameState));
    }
    /**
     * @brief Test sprawdzający sytuację braku remisu w grze.
     *
     * @details Plansza nie jest w pełni wypełniona i żaden gracz nie wygrywa.
     */
    @Test
    public void testNoDrawCondition() {
        String[][] gameState = {
                {"X", "O", null},
                {"O", "X", "O"},
                {"O", "X", "O"}
        };
        assertNull(GameLogic.getWinner(gameState));
    }
}
