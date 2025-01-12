package kolkoikrzyzyk;

public class UserStats {
    private int gamesPlayed;
    private int wins;
    private int losses;
    private int gamesVsBot;
    private int gamesLocal;

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

