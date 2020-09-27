package commands.fun.tictactoe;

public class TicTacToeStatistic {
    private final long userId;
    private final int wins;
    private final int draws;
    private final int loses;

    public TicTacToeStatistic(long userId, int wins, int draws, int loses) {
        this.userId = userId;
        this.wins = wins;
        this.draws = draws;
        this.loses = loses;
    }

    public long getUserId() {
        return userId;
    }

    public int getWins() {
        return wins;
    }

    public int getDraws() {
        return draws;
    }

    public int getLoses() {
        return loses;
    }
}
