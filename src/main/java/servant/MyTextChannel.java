package servant;

import net.dv8tion.jda.api.JDA;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static servant.Database.closeQuietly;

public class MyTextChannel {
    private final JDA jda;
    private final long guildId;
    private final long tcId;

    public MyTextChannel(JDA jda, long guildId, long tcId) {
        this.jda = jda;
        this.guildId = guildId;
        this.tcId = tcId;
    }

    public boolean containsBirthdayList() {
        Connection connection = null;
        var contains = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT list_tc_id " +
                            "FROM guild_birthdays " +
                            "WHERE guild_id=?");
            preparedStatement.setLong(1, guildId);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) contains = tcId == resultSet.getLong("list_tc_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyTextChannel#containsBirthdayList"));
        } finally {
            closeQuietly(connection);
        }

        return contains;
    }

    public boolean isBirthdayAnnouncementTc() {
        Connection connection = null;
        var contains = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT announcement_tc_id " +
                            "FROM guild_birthdays " +
                            "WHERE guild_id=?");
            preparedStatement.setLong(1, guildId);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) contains = tcId == resultSet.getLong("announcement_tc_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyTextChannel#isBirthdayAnnouncementTc"));
        } finally {
            closeQuietly(connection);
        }

        return contains;
    }
}
