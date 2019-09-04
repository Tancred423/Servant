// Author: Tancred423 (https://github.com/Tancred423)
package useful.votes;

import servant.Database;

import java.sql.SQLException;

public class VotesDatabase {
    // Votes
    public static void setVote(long messageId, long authorId, String type) throws SQLException {
        var connection = Database.getConnection();
        var insert = connection.prepareStatement("INSERT INTO votes (message_id,author_id,type) VALUES (?,?,?)");
        insert.setLong(1, messageId);
        insert.setLong(2, authorId);
        insert.setString(3, type);
        insert.executeUpdate();
        connection.close();
    }

    public static void unsetVote(long messageId) throws SQLException {
        var connection = Database.getConnection();
        var delete = connection.prepareStatement("DELETE FROM votes WHERE message_id=?");
        delete.setLong(1, messageId);
        delete.executeUpdate();
        connection.close();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isQuickvote(long messageId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT type FROM votes WHERE message_id=?");
        select.setLong(1, messageId);
        var resultSet = select.executeQuery();
        var isQuickvote = false;
        if (resultSet.first()) if (resultSet.getString("type").equals("quick")) isQuickvote = true;
        connection.close();
        return isQuickvote;
    }

    public static boolean isVote(long messageId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT type FROM votes WHERE message_id=?");
        select.setLong(1, messageId);
        var resultSet = select.executeQuery();
        var isQuickvote = false;
        if (resultSet.first()) if (resultSet.getString("type").equals("vote")) isQuickvote = true;
        connection.close();
        return isQuickvote;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isRadioVote(long messageId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT type FROM votes WHERE message_id=?");
        select.setLong(1, messageId);
        var resultSet = select.executeQuery();
        var isQuickvote = false;
        if (resultSet.first()) if (resultSet.getString("type").equals("radio")) isQuickvote = true;
        connection.close();
        return isQuickvote;
    }

    public static long getAuthorId(long messageId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT author_id FROM votes WHERE message_id=?");
        select.setLong(1, messageId);
        var resultSet = select.executeQuery();
        var authorId = 0L;
        if (resultSet.first()) authorId = resultSet.getLong("author_id");
        connection.close();
        return authorId;
    }

    // User Votes
    public static void setUserVote(long messageId, long userId, long emoteId, String emoji) throws SQLException {
        System.out.println(emoji.length());
        var connection = Database.getConnection();
        var insert = connection.prepareStatement("INSERT INTO user_votes (message_id,user_id,emote_id,emoji) VALUES (?,?,?,?)");
        insert.setLong(1, messageId);
        insert.setLong(2, userId);
        insert.setLong(3, emoteId);
        insert.setString(4, emoji);
        insert.executeUpdate();
        connection.close();
    }

    public static void unsetUserVote(long messageId, long userId) throws SQLException {
        var connection = Database.getConnection();
        var delete = connection.prepareStatement("DELETE FROM user_votes WHERE message_id=? AND user_id=?");
        delete.setLong(1, messageId);
        delete.setLong(2, userId);
        delete.executeUpdate();
        connection.close();
    }

    public static void unsetUserVotes(long messageId) throws SQLException {
        var connection = Database.getConnection();
        var delete = connection.prepareStatement("DELETE FROM user_votes WHERE message_id=?");
        delete.setLong(1, messageId);
        delete.executeUpdate();
        connection.close();
    }

    public static boolean hasVoted(long messageId, long userId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM user_votes WHERE message_id=? AND user_id=?");
        select.setLong(1, messageId);
        select.setLong(2, userId);
        var resultSet = select.executeQuery();
        var hasVoted = false;
        if (resultSet.first()) hasVoted = true;
        connection.close();
        return hasVoted;
    }

    public static long getVoteEmoteId(long messageId, long userId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT emote_id FROM user_votes WHERE message_id=? AND user_id=?");
        select.setLong(1, messageId);
        select.setLong(2, userId);
        var resultSet = select.executeQuery();
        var id = 0L;
        if (resultSet.first()) id = resultSet.getLong("emote_id");
        connection.close();
        return id;
    }

    public static String getVoteEmoji(long messageId, long userId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT emoji FROM user_votes WHERE message_id=? AND user_id=?");
        select.setLong(1, messageId);
        select.setLong(2, userId);
        var resultSet = select.executeQuery();
        var emote = "";
        if (resultSet.first()) emote = resultSet.getString("emoji");
        connection.close();
        return emote;
    }
}
