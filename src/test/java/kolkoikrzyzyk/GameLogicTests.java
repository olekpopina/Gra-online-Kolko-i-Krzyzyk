package kolkoikrzyzyk;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


public class GameLogicTests {

    @Test
    public void testGetWinnerForRows() {
        String[][] gameState = {
                {"X", "X", "X"},
                {null, null, null},
                {null, null, null}
        };
        assertEquals("X", GameLogic.getWinner(gameState));
    }

    @Test
    public void testGetWinnerForColumns() {
        String[][] gameState = {
                {"O", null, null},
                {"O", null, null},
                {"O", null, null}
        };
        assertEquals("O", GameLogic.getWinner(gameState));
    }

    @Test
    public void testGetWinnerForDiagonals() {
        String[][] gameState = {
                {"X", null, null},
                {null, "X", null},
                {null, null, "X"}
        };
        assertEquals("X", GameLogic.getWinner(gameState));
    }

    @Test
    public void testNoWinnerDraw() {
        String[][] gameState = {
                {"X", "O", "X"},
                {"O", "X", "O"},
                {"O", "X", "O"}
        };
        assertNull(GameLogic.getWinner(gameState));
    }

    @Test
    public void testNoWinnerEmptyBoard() {
        String[][] gameState = {
                {null, null, null},
                {null, null, null},
                {null, null, null}
        };
        assertNull(GameLogic.getWinner(gameState));
    }

    @Test
    public void testWinRatioCalculation() {
        UserStats stats = new UserStats(10, 6, 4, 2, 4);
        assertEquals(0.6, stats.getWinRatio(), 0.01);
    }

    @Test
    public void testNoGamesPlayedWinRatio() {
        UserStats stats = new UserStats(0, 0, 0, 0, 0);
        assertEquals(0.0, stats.getWinRatio(), 0.01);
    }

    @Test
    public void testDatabaseUserRegistration() {
        DatabaseManager.registerUser("TestUser", "password");
        assertTrue(DatabaseManager.authenticateUser("TestUser", "password"));

        DatabaseManager.deleteUser("TestUser");
    }

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

    @Test
    public void testVsComputerGameMove() {
        VsComputerGame game = new VsComputerGame();
        game.makeMove(0, 0);
        assertEquals("X", game.gameState[0][0]);
    }

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

    @Test
    public void testServerGameConnection() {
        ServerGame server = new ServerGame();
        assertDoesNotThrow(server::startServer);
    }

    @Test
    public void testClientGameConnection() {
        ServerGame server = new ServerGame();
        server.startServer();
        assertDoesNotThrow(() -> {
            ClientGame client = new ClientGame("localhost");
            client.connectToServer("localhost");
        });
    }

    @Test
    public void testDrawCondition() {
        String[][] gameState = {
                {"X", "O", "X"},
                {"O", "X", "O"},
                {"O", "X", "O"}
        };
        assertNull(GameLogic.getWinner(gameState));
    }

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
