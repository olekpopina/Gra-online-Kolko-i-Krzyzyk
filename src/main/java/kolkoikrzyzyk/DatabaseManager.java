package kolkoikrzyzyk;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:game_database.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC"); // Завантажуємо драйвер
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Nie znaleziono sterownika SQLite JDBC!", e);
        }
    }

    // Створюємо з'єднання з базою даних
    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("Nie udało się połączyć z bazą danych", e);
        }
    }

    // Ініціалізація таблиць
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

        String createGamesTable = "CREATE TABLE IF NOT EXISTS games (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "player1 TEXT NOT NULL, " +
                "player2 TEXT NOT NULL, " +
                "winner TEXT, " +
                "game_mode TEXT NOT NULL, " + // local, vs_bot, online
                "game_result TEXT, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createGamesTable);
        } catch (SQLException e) {
            throw new RuntimeException("Nie udało się utworzyć tabel", e);
        }
    }

    // Перевірка логіна і пароля
    public static boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Повертає true, якщо користувач знайдений
        } catch (SQLException e) {
            throw new RuntimeException("Nie udało się zalogować", e);
        }
    }

    // Реєстрація нового користувача
    public static boolean registerUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false; // Якщо ім'я користувача вже існує
        }
    }

    // Отримання статистики користувача
    public static String getPlayerStatistics(String username) {
        String query = "SELECT games_played, wins, losses, games_vs_bot, games_local FROM users WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return String.format("Rozegrane gry: %d | Wygrane: %d | Przegrane: %d | Przeciw botom: %d | Lokalnie: %d",
                        rs.getInt("games_played"),
                        rs.getInt("wins"),
                        rs.getInt("losses"),
                        rs.getInt("games_vs_bot"),
                        rs.getInt("games_local"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nie udało się pobrać statystyk", e);
        }
        return "Brak danych";
    }

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

    public static void updateUserStats(String username, boolean win, String gameMode) {

        String query = "UPDATE users " +
                "SET games_played = games_played + CASE WHEN ? THEN 1 ELSE 0 END, " + // Інкремент для онлайн-режиму
                "wins = wins + CASE WHEN ? AND ? THEN 1 ELSE 0 END, " + // Інкрементуємо перемоги, якщо виграв і режим онлайн
                "losses = losses + CASE WHEN NOT ? AND ? THEN 1 ELSE 0 END, " + // Інкрементуємо поразки, якщо програв і режим онлайн
                "games_vs_bot = games_vs_bot + CASE WHEN ? THEN 1 ELSE 0 END, " + // Інкрементуємо проти бота, якщо виграв
                "games_local = games_local + CASE WHEN ? THEN 1 ELSE 0 END " + // Інкрементуємо локальні ігри
                "WHERE username = ?";


        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, gameMode.equals("online")); // Інкремент для онлайн-режиму
            pstmt.setBoolean(2, win); // Інкремент для перемог
            pstmt.setBoolean(3, gameMode.equals("online")); // Умови для перемог (режим онлайн)
            pstmt.setBoolean(4, win); // Інкремент для поразок (NOT win)
            pstmt.setBoolean(5, gameMode.equals("online")); // Умови для поразок (режим онлайн)
            pstmt.setBoolean(6, gameMode.equals("vs_bot") && win); // Інкремент для ігор проти бота
            pstmt.setBoolean(7, gameMode.equals("local")); // Інкремент для локальних ігор
            pstmt.setString(8, username); // Оновлюємо для конкретного користувача

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Nie udało się zaktualizować statystyk gracza", e);
        }

    }

    public static boolean changePassword(String username, String oldPassword, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newPassword); // Хешуйте пароль, якщо потрібно
            pstmt.setString(2, username);
            pstmt.setString(3, oldPassword); // Перевіряємо старий пароль
            return pstmt.executeUpdate() > 0; // Повертаємо true, якщо оновлено хоча б 1 запис
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


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
        return null; // Якщо статистику не вдалося отримати
    }



}
