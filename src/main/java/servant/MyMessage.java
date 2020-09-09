package servant;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import plugins.moderation.customcommands.CustomCommandsHandler;
import utilities.MessageUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static servant.Database.closeQuietly;

public class MyMessage {
    private final JDA jda;
    private final long guildId;
    private final long tcId;
    private final long msgId;
    private String content;
    private User author;

    public MyMessage(JDA jda, long guildId, long tcId, long msgId) {
        this.jda = jda;
        this.guildId = guildId;
        this.tcId = tcId;
        this.msgId = msgId;
    }

    public boolean isBirthdayList() {
        Connection connection = null;
        var isBirthdayList = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT list_msg_id " +
                            "FROM guild_birthdays " +
                            "WHERE guild_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setLong(1, guildId);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) isBirthdayList = msgId == resultSet.getLong("list_msg_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyMessage#isBirthdayList"));
        } finally {
            closeQuietly(connection);
        }

        return isBirthdayList;
    }

    public boolean isBestOfImageBlacklisted() {
        Connection connection = null;
        var isBl = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT msg_id " +
                            "FROM tmp_best_of_image_bl " +
                            "WHERE msg_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setLong(1, msgId);
            var resultSet = preparedStatement.executeQuery();
            isBl = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyMessage#isBestOfImageBlacklisted"));
        } finally {
            closeQuietly(connection);
        }

        return isBl;
    }

    public boolean isBestOfQuoteBlacklisted() {
        Connection connection = null;
        var isBl = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT msg_id " +
                            "FROM tmp_best_of_quote_bl " +
                            "WHERE msg_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setLong(1, msgId);
            var resultSet = preparedStatement.executeQuery();
            isBl = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyMessage#isBestOfQuoteBlacklisted"));
        } finally {
            closeQuietly(connection);
        }

        return isBl;
    }

    public boolean isGiveaway() {
        Connection connection = null;
        var isGiveaway = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT id " +
                            "FROM giveaways " +
                            "WHERE guild_id=? " +
                            "AND tc_id=? " +
                            "AND msg_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, tcId);
            preparedStatement.setLong(3, msgId);
            var resultSet = preparedStatement.executeQuery();
            isGiveaway = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyMessage#isGiveaway"));
        } finally {
            closeQuietly(connection);
        }

        return isGiveaway;
    }

    public boolean isQuickpoll() {
        Connection connection = null;
        var isQuickpoll = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT p.id " +
                            "FROM polls AS p " +
                            "INNER JOIN const_poll_types AS t " +
                            "ON p.poll_type_id=t.id " +
                            "WHERE p.guild_id=? " +
                            "AND p.tc_id=? " +
                            "AND p.msg_id=? " +
                            "AND t.poll_type='quick'",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, tcId);
            preparedStatement.setLong(3, msgId);
            var resultSet = preparedStatement.executeQuery();
            isQuickpoll = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyMessage#isQuickpoll"));
        } finally {
            closeQuietly(connection);
        }

        return isQuickpoll;
    }

    public boolean isCheckpoll() {
        Connection connection = null;
        var isCheckpoll = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT p.id " +
                            "FROM polls AS p " +
                            "INNER JOIN const_poll_types AS t " +
                            "ON p.poll_type_id=t.id " +
                            "WHERE p.guild_id=? " +
                            "AND p.tc_id=? " +
                            "AND p.msg_id=? " +
                            "AND t.poll_type='check'",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, tcId);
            preparedStatement.setLong(3, msgId);
            var resultSet = preparedStatement.executeQuery();
            isCheckpoll = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyMessage#isCheckpoll"));
        } finally {
            closeQuietly(connection);
        }

        return isCheckpoll;
    }

    public boolean isRadiopoll() {
        Connection connection = null;
        var isRadiopoll = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT p.id " +
                            "FROM polls AS p " +
                            "INNER JOIN const_poll_types AS t " +
                            "ON p.poll_type_id=t.id " +
                            "WHERE p.guild_id=? " +
                            "AND p.tc_id=? " +
                            "AND p.msg_id=? " +
                            "AND t.poll_type='radio'",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, tcId);
            preparedStatement.setLong(3, msgId);
            var resultSet = preparedStatement.executeQuery();
            isRadiopoll = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyMessage#isRadiopoll"));
        } finally {
            closeQuietly(connection);
        }

        return isRadiopoll;
    }

    public boolean isRemindMe() {
        Connection connection = null;
        var isRemindMe = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT id " +
                            "FROM remind_mes " +
                            "WHERE msg_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setLong(1, msgId);
            var resultSet = preparedStatement.executeQuery();
            isRemindMe = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyMessage#isRemindMe"));
        } finally {
            closeQuietly(connection);
        }

        return isRemindMe;
    }

    public boolean isSignup() {
        Connection connection = null;
        var isSignup = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT id " +
                            "FROM signups " +
                            "WHERE msg_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setLong(1, msgId);
            var resultSet = preparedStatement.executeQuery();
            isSignup = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyMessage#isSignup"));
        } finally {
            closeQuietly(connection);
        }

        return isSignup;
    }

    public boolean isRating() {
        Connection connection = null;
        var isRemindMe = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT id " +
                            "FROM ratings " +
                            "WHERE msg_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setLong(1, msgId);
            var resultSet = preparedStatement.executeQuery();
            isRemindMe = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyMessage#isRating"));
        } finally {
            closeQuietly(connection);
        }

        return isRemindMe;
    }

    public boolean isReactionRole() {
        Connection connection = null;
        var isRemindMe = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT id " +
                            "FROM reaction_role_messages " +
                            "WHERE msg_id=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setLong(1, msgId);
            var resultSet = preparedStatement.executeQuery();
            isRemindMe = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "MyMessage#isReactionRole"));
        } finally {
            closeQuietly(connection);
        }

        return isRemindMe;
    }

    public boolean isCustomCommand() {
        var customCommand = CustomCommandsHandler.getCustomCommandFromInvokeOrAlias(jda, guildId, MessageUtil.removePrefix(jda, guildId, true, content.split(" ")[0]));
        return customCommand != null;
    }

    public boolean startsWithPrefix() {
        var sm = jda.getShardManager();
        if (sm != null) {
            var guild = sm.getGuildById(guildId);
            if (guild != null) {
                var prefix = new MyGuild(guild).getPrefix();
                return content.startsWith(prefix);
            }
        }
        return false;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
