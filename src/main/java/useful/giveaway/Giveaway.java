package useful.giveaway;

import net.dv8tion.jda.api.entities.User;
import servant.Log;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import static utilities.DatabaseConn.closeQuietly;

public class Giveaway {
    private long guildId;   // key
    private long channelId; // key
    private long messageId; // key
    private long hostId;
    private String prize;
    private Timestamp time;
    private int amountWinners;

    public Giveaway(long guildId, long channelId, long messageId, User botUser) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.messageId = messageId;

        setAttributes(botUser);
    }

    private void setAttributes(User botUser) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM giveawaylist WHERE guild_id=? AND channel_id=? AND message_id=?");
            select.setLong(1, guildId);
            select.setLong(2, channelId);
            select.setLong(3, messageId);
            var resultSet = select.executeQuery();

            if (resultSet.first()) {
                hostId = resultSet.getLong("host_id");
                prize = resultSet.getString("prize");
                time = resultSet.getTimestamp("time");
                amountWinners = resultSet.getInt("amount_winners");
            }
        } catch (SQLException e) {
            new Log(e, null, botUser, "giveaway", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public long getHostId() {
        return hostId;
    }

    public String getPrize() {
        return prize;
    }

    public Timestamp getTime() {
        return time;
    }

    public int getAmountWinners() {
        return amountWinners;
    }
}
