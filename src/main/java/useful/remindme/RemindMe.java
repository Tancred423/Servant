package useful.remindme;

import net.dv8tion.jda.api.JDA;
import servant.LoggingTask;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static servant.Database.closeQuietly;

public class RemindMe {
    private int aiNumber;
    private long guildId;
    private long channelId;
    private long messageId;
    private long userId;
    private Timestamp eventTime;
    private String topic;

    public RemindMe(int aiNumber, long guildId, long channelId, long messageId, long userId, Timestamp eventTime, String topic) {
        this.aiNumber = aiNumber;
        this.guildId = guildId;
        this.channelId = channelId;
        this.messageId = messageId;
        this.userId = userId;
        this.eventTime = eventTime;
        this.topic = topic;
    }

    public int getAiNumber() { return aiNumber; }
    public long getGuildId() { return guildId; }
    public long getChannelId() { return channelId; }
    public long getMessageId() { return messageId; }
    public long getUserId() { return userId; }
    public Timestamp getEventTime() { return eventTime; }
    public String getTopic() { return topic; }

    public static List<RemindMe> getList(JDA jda) {
        Connection connection = null;
        var alarms = new ArrayList<RemindMe>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM remindme_new");
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do {
                    alarms.add(new RemindMe(
                            resultSet.getInt("ai_number"),
                            resultSet.getLong("guild_id"),
                            resultSet.getLong("channel_id"),
                            resultSet.getLong("message_id"),
                            resultSet.getLong("user_id"),
                            resultSet.getTimestamp("event_time"),
                            resultSet.getString("topic")
                    ));
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            new LoggingTask(e, jda, "RemindMe#getList");
        } finally {
            closeQuietly(connection);
        }

        return alarms;
    }
}
