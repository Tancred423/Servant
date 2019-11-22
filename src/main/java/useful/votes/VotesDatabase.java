// Author: Tancred423 (https://github.com/Tancred423)
package useful.votes;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import servant.Log;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;

import static utilities.DatabaseConn.closeQuietly;

public class VotesDatabase {
    // Votes
    public static void setVote(long messageId, long authorId, String type, Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("INSERT INTO votes (message_id,author_id,type) VALUES (?,?,?)");
            insert.setLong(1, messageId);
            insert.setLong(2, authorId);
            insert.setString(3, type);
            insert.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public static void unsetVote(long messageId, Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM votes WHERE message_id=?");
            delete.setLong(1, messageId);
            delete.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isQuickvote(long messageId, Guild guild, User user) {
        Connection connection = null;
        var isQuickvote = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT type FROM votes WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) if (resultSet.getString("type").equals("quick")) isQuickvote = true;
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isQuickvote;
    }

    public static boolean isVote(long messageId, Guild guild, User user) {
        Connection connection = null;
        var isVote = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT type FROM votes WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) if (resultSet.getString("type").equals("vote")) isVote = true;
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isVote;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isRadioVote(long messageId, Guild guild, User user) {
        Connection connection = null;
        var isRadiovote = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT type FROM votes WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) if (resultSet.getString("type").equals("radio")) isRadiovote = true;
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isRadiovote;
    }

    public static long getAuthorId(long messageId, Guild guild, User user) {
        Connection connection = null;
        var authorId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT author_id FROM votes WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) authorId = resultSet.getLong("author_id");
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return authorId;
    }

    // User Votes
    public static void setUserVote(long messageId, long userId, long emoteId, String emoji, Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("INSERT INTO user_votes (message_id,user_id,emote_id,emoji) VALUES (?,?,?,?)");
            insert.setLong(1, messageId);
            insert.setLong(2, userId);
            insert.setLong(3, emoteId);
            insert.setString(4, emoji);
            insert.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public static void unsetUserVote(long messageId, long userId, Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM user_votes WHERE message_id=? AND user_id=?");
            delete.setLong(1, messageId);
            delete.setLong(2, userId);
            delete.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public static void unsetUserVotes(long messageId, Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM user_votes WHERE message_id=?");
            delete.setLong(1, messageId);
            delete.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public static boolean hasVoted(long messageId, long userId, Guild guild, User user) {
        Connection connection = null;
        var hasVoted = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM user_votes WHERE message_id=? AND user_id=?");
            select.setLong(1, messageId);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) hasVoted = true;
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasVoted;
    }

    public static long getVoteEmoteId(long messageId, long userId, Guild guild, User user) {
        Connection connection = null;
        var id = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT emote_id FROM user_votes WHERE message_id=? AND user_id=?");
            select.setLong(1, messageId);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) id = resultSet.getLong("emote_id");
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return id;
    }

    public static String getVoteEmoji(long messageId, long userId, Guild guild, User user) {
        Connection connection = null;
        var emote = "";

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT emoji FROM user_votes WHERE message_id=? AND user_id=?");
            select.setLong(1, messageId);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) emote = resultSet.getString("emoji");
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return emote;
    }
}
