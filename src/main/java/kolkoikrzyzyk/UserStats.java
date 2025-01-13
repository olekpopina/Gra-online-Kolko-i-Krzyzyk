package kolkoikrzyzyk;

/**
 * @brief Reprezentuje statystyki użytkownika w grze "Kółko i Krzyżyk".
 *
 * @details Klasa przechowuje szczegółowe informacje o wynikach użytkownika,
 * w tym liczbę rozegranych gier, wygranych gier, przegranych gier,
 * gier przeciwko komputerowi oraz gier lokalnych.
 */
public class UserStats {
    private final int gamesPlayed; ///< Liczba wszystkich rozegranych gier.
    private final int wins; ///< Liczba wygranych gier.
    private final int losses; ///< Liczba przegranych gier.
    private final int gamesVsBot; ///< Liczba gier rozegranych przeciwko komputerowi.
    private final int gamesLocal; ///< Liczba gier rozegranych lokalnie.

    /**
     * @brief Konstruktor inicjalizujący obiekt UserStats.
     *
     * @param gamesPlayed Liczba wszystkich rozegranych gier.
     * @param wins Liczba wygranych gier.
     * @param losses Liczba przegranych gier.
     * @param gamesVsBot Liczba gier rozegranych przeciwko komputerowi.
     * @param gamesLocal Liczba gier rozegranych lokalnie.
     */
    public UserStats(int gamesPlayed, int wins, int losses, int gamesVsBot, int gamesLocal) {
        this.gamesPlayed = gamesPlayed;
        this.wins = wins;
        this.losses = losses;
        this.gamesVsBot = gamesVsBot;
        this.gamesLocal = gamesLocal;
    }

    /**
     * @brief Pobiera liczbę rozegranych gier.
     *
     * @return Liczba wszystkich rozegranych gier.
     */
    public int getGamesPlayed() {
        return gamesPlayed;
    }

    /**
     * @brief Pobiera liczbę wygranych gier.
     *
     * @return Liczba wygranych gier.
     */
    public int getWins() {
        return wins;
    }

    /**
     * @brief Pobiera liczbę przegranych gier.
     *
     * @return Liczba przegranych gier.
     */
    public int getLosses() {
        return losses;
    }

    /**
     * @brief Pobiera liczbę gier rozegranych przeciwko komputerowi.
     *
     * @return Liczba gier rozegranych przeciwko komputerowi.
     */
    public int getGamesVsBot() {
        return gamesVsBot;
    }

    /**
     * @brief Pobiera liczbę gier rozegranych lokalnie.
     *
     * @return Liczba gier rozegranych lokalnie.
     */
    public int getGamesLocal() {
        return gamesLocal;
    }

    /**
     * @brief Oblicza stosunek wygranych do rozegranych gier.
     *
     * @details Jeśli użytkownik nie rozegrał żadnych gier, stosunek wynosi 0.0.
     *
     * @return Stosunek wygranych do rozegranych gier (double).
     */
    public double getWinRatio() {
        return gamesPlayed > 0 ? (double) wins / gamesPlayed : 0.0;
    }
}
