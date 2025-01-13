package kolkoikrzyzyk;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief
 * Klasa zarządzająca połączeniem z bazą danych oraz operacjami na użytkownikach i ich statystykach.
 */
public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:game_database.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC"); // Ładowanie sterownika SQLite
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Nie znaleziono sterownika SQLite JDBC!", e);
        }
    }

    /**
     * Tworzy połączenie z bazą danych.
     * @return Obiekt połączenia.
     */
    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("Nie udało się połączyć z bazą danych", e);
        }
    }

    /**
     * Inicjalizuje tabele w bazie danych, jeśli jeszcze nie istnieją.
     */
    public static void initializeDatabase() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "games_played INTEGER DEFAULT 0, " +
                "wins INTEGER DEFAULT 0, " +
                "losses INTEGER DEFAULT 0, " +
                "games_vs_bot INTEGER DEFAULT 0, " +
                "games_local INTEGER DEFAULT 0)";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
        } catch (SQLException e) {
            throw new RuntimeException("Nie udało się utworzyć tabel", e);
        }
    }

    /**
     * Sprawdza poprawność logowania użytkownika.
     * @param username Nazwa użytkownika.
     * @param password Hasło użytkownika.
     * @return True, jeśli dane są poprawne, false w przeciwnym razie.
     */
    public static boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Nie udało się zalogować", e);
        }
    }

    /**
     * Rejestruje nowego użytkownika w bazie danych.
     * @param username Nazwa użytkownika.
     * @param password Hasło użytkownika.
     * @return True, jeśli rejestracja zakończyła się sukcesem, false jeśli użytkownik już istnieje.
     */
    public static boolean registerUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Pobiera listę najlepszych graczy.
     * @return Lista najlepszych graczy w formacie "username: wins/gamesPlayed (vs Bot: gamesVsBot)".
     */
    public static List<String> getTopPlayers() {
        String query = "SELECT username, wins, games_played, games_vs_bot " +
                "FROM users ORDER BY wins DESC LIMIT 10";

        List<String> topPlayers = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                int wins = rs.getInt("wins");
                int gamesPlayed = rs.getInt("games_played");
                int gamesVsBot = rs.getInt("games_vs_bot");

                topPlayers.add(String.format("%s: %d/%d (vs Bot: %d)", username, wins, gamesPlayed, gamesVsBot));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Nie udało się pobrać listy najlepszych graczy", e);
        }

        return topPlayers;
    }

    /**
     * Aktualizuje statystyki użytkownika w zależności od wyniku gry i trybu gry.
     * @param username Nazwa użytkownika.
     * @param win True, jeśli użytkownik wygrał, false w przeciwnym razie.
     * @param gameMode Tryb gry: "online", "vs_bot" lub "local".
     */
    public static void updateUserStats(String username, boolean win, String gameMode) {

        String query = "UPDATE users " +
                "SET games_played = games_played + CASE WHEN ? THEN 1 ELSE 0 END, " +
                "wins = wins + CASE WHEN ? AND ? THEN 1 ELSE 0 END, " +
                "losses = losses + CASE WHEN NOT ? AND ? THEN 1 ELSE 0 END, " +
                "games_vs_bot = games_vs_bot + CASE WHEN ? THEN 1 ELSE 0 END, " +
                "games_local = games_local + CASE WHEN ? THEN 1 ELSE 0 END " +
                "WHERE username = ?";


        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, gameMode.equals("online"));
            pstmt.setBoolean(2, win);
            pstmt.setBoolean(3, gameMode.equals("online"));
            pstmt.setBoolean(4, win);
            pstmt.setBoolean(5, gameMode.equals("online"));
            pstmt.setBoolean(6, gameMode.equals("vs_bot") && win);
            pstmt.setBoolean(7, gameMode.equals("local"));
            pstmt.setString(8, username);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Nie udało się zaktualizować statystyk gracza", e);
        }

    }

    /**
     * Zmienia hasło użytkownika.
     * @param username Nazwa użytkownika.
     * @param oldPassword Stare hasło użytkownika.
     * @param newPassword Nowe hasło użytkownika.
     * @return True, jeśli hasło zostało zmienione, false w przeciwnym razie.
     */
    public static boolean changePassword(String username, String oldPassword, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            pstmt.setString(3, oldPassword);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Pobiera statystyki użytkownika.
     * @param username Nazwa użytkownika.
     * @return Obiekt UserStats zawierający statystyki użytkownika lub null, jeśli użytkownik nie istnieje.
     */
    public static UserStats getUserStats(String username) {
        String query = "SELECT games_played, wins, losses, games_vs_bot, games_local " +
                "FROM users WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int gamesPlayed = rs.getInt("games_played");
                int wins = rs.getInt("wins");
                int losses = rs.getInt("losses");
                int gamesVsBot = rs.getInt("games_vs_bot");
                int gamesLocal = rs.getInt("games_local");

                return new UserStats(gamesPlayed, wins, losses, gamesVsBot, gamesLocal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * @brief Usuwa użytkownika z bazy danych.
     *
     * @param username Nazwa użytkownika, który ma zostać usunięty.
     * @return true, jeśli użytkownik został pomyślnie usunięty, false w przypadku błędu lub gdy użytkownik nie istnieje.
     *
     * @details Metoda korzysta z zapytania SQL, aby usunąć użytkownika z tabeli `users` na podstawie podanej nazwy użytkownika.
     *          Jeśli operacja zakończy się powodzeniem i co najmniej jeden wiersz zostanie usunięty, metoda zwraca `true`.
     *          W przeciwnym razie zwraca `false`.
     *          W przypadku wystąpienia wyjątku SQL, błąd jest drukowany w konsoli.
     */
    public static boolean deleteUser(String username) {
        String query = "DELETE FROM users WHERE username = ?"; //Usunnięcia użytkownika
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
