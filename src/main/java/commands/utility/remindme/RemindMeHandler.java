package commands.utility.remindme;

import net.dv8tion.jda.api.JDA;
import servant.LoggingTask;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;

import static servant.Database.closeQuietly;

public class RemindMeHandler {
        // RemindMe
    public static RemindMe getRemindMe(JDA jda, int aiNumber) {
        Connection connection = null;
        RemindMe remindMe = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * " +
                            "FROM remind_mes " +
                            "WHERE id=?");
            select.setInt(1, aiNumber);
            var resultSet = select.executeQuery();
            if (resultSet.next()) {
                remindMe = new RemindMe(
                        jda,
                        resultSet.getLong("guild_id"),
                        resultSet.getLong("tc_id"),
                        resultSet.getLong("msg_id")
                );
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getRemindMe"));
        } finally {
            closeQuietly(connection);
        }

        return remindMe;
    }
}
