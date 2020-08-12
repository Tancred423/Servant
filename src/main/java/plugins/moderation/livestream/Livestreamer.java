package plugins.moderation.livestream;

public class Livestreamer {
    private final long guildId;
    private final long userId;

    public Livestreamer(long guildId, long userId) {
        this.guildId = guildId;
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public long getGuildId() {
        return guildId;
    }
}
