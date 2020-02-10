package useful.giveaway;

import net.dv8tion.jda.api.JDA;
import servant.LoggingTask;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import static servant.Database.closeQuietly;

public class Giveaway {
    private JDA jda;

    private long guildId;   // key
    private long channelId; // key
    private long messageId; // key
    private long hostId;
    private String prize;
    private Timestamp time;
    private int amountWinners;

    public Giveaway(JDA jda, long guildId, long channelId, long messageId) {
        this.jda = jda;

        this.guildId = guildId;
        this.channelId = channelId;
        this.messageId = messageId;

        setAttributes();
    }

    private void setAttributes() {
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
            new LoggingTask(e, jda, "Giveaway#setAttributes");
        } finally {
            closeQuietly(connection);
        }
    }

    public long getHostId() { return hostId; }
    public String getPrize() { return prize; }
    public Timestamp getTime() { return time; }
    public int getAmountWinners() { return amountWinners; }
}
