package useful.remindme;

import servant.Log;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static servant.Database.closeQuietly;

public class RemindMe {
    private int aiNumber;
    private long userId;
    private Timestamp eventTime;
    private String topic;

    public RemindMe(int aiNumber, long userId, Timestamp eventTime, String topic) {
        this.aiNumber = aiNumber;
        this.userId = userId;
        this.eventTime = eventTime;
        this.topic = topic;
    }

    public int getAiNumber() { return aiNumber; }
    public long getUserId() { return userId; }
    public Timestamp getEventTime() { return eventTime; }
    public String getTopic() { return topic; }

    public static List<RemindMe> getRemindMeList() {
        Connection connection = null;
        var alarms = new ArrayList<RemindMe>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM remindme");
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do alarms.add(new RemindMe(resultSet.getInt("ai_number"), resultSet.getLong("user_id"), resultSet.getTimestamp("event_time"), resultSet.getString("topic")));
                while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, null, null, "RemindMes.java", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return alarms;
    }
}
