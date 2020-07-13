package commands.utility.rate;

public class Vote {
    private final long userId;
    private final String reaction;

    public Vote(long userId, String reaction) {
        this.userId = userId;
        this.reaction = reaction;
    }

    public long getUserId() {
        return userId;
    }

    public String getReaction() {
        return reaction;
    }
}
