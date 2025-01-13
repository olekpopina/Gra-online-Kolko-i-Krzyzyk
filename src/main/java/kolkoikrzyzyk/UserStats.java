package kolkoikrzyzyk;

public class UserStats {
    private final int gamesPlayed;
    private final int wins;
    private final int losses;
    private final int gamesVsBot;
    private final int gamesLocal;

    public UserStats(int gamesPlayed, int wins, int losses, int gamesVsBot, int gamesLocal) {
        this.gamesPlayed = gamesPlayed;
        this.wins = wins;
        this.losses = losses;
        this.gamesVsBot = gamesVsBot;
        this.gamesLocal = gamesLocal;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getGamesVsBot() {
        return gamesVsBot;
    }

    public int getGamesLocal() {
        return gamesLocal;
    }

    public double getWinRatio() {
        return gamesPlayed > 0 ? (double) wins / gamesPlayed : 0.0;
    }
}

