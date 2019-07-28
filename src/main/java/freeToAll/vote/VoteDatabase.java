package freeToAll.vote;

import servant.Database;

import java.sql.SQLException;

public class VoteDatabase {
    // Votes
    static void setQuickvote(long messageId, long authorId, String type) throws SQLException {
        var connection = Database.getConnection();
        var preparedStatement = connection.prepareStatement("INSERT INTO votes (message_id,author_id,type) VALUES (?,?,?)");
        preparedStatement.setLong(1, messageId);
        preparedStatement.setLong(2, authorId);
        preparedStatement.setString(3, type);
        preparedStatement.executeUpdate();
        connection.close();
    }

    static void unsetQuickvote(long messageId) throws SQLException {
        var connection = Database.getConnection();
        var preparedStatement = connection.prepareStatement("DELETE FROM votes WHERE message_id=?");
        preparedStatement.setLong(1, messageId);
        preparedStatement.executeUpdate();
        connection.close();
    }

    static boolean isQuickvote(long messageId) throws SQLException {
        var connection = Database.getConnection();
        var preparedStatement = connection.prepareStatement("SELECT type FROM votes WHERE message_id=?");
        preparedStatement.setLong(1, messageId);
        var resultSet = preparedStatement.executeQuery();
        boolean isQuickvote = false;
        if (resultSet.first()) if (resultSet.getString("type").equals("quick")) isQuickvote = true;
        connection.close();
        return isQuickvote;
    }

    static long getAuthorId(long messageId) throws SQLException {
        var connection = Database.getConnection();
        var preparedStatement = connection.prepareStatement("SELECT author_id FROM votes WHERE message_id=?");
        preparedStatement.setLong(1, messageId);
        var resultSet = preparedStatement.executeQuery();
        long authorId = 0;
        if (resultSet.first()) authorId = resultSet.getLong("author_id");
        connection.close();
        return authorId;
    }

    // User Votes.
    static void setUserVote(long messageId, long userId) throws SQLException {
        var connection = Database.getConnection();
        var preparedStatement = connection.prepareStatement("INSERT INTO user_votes (message_id,user_id) VALUES (?,?)");
        preparedStatement.setLong(1, messageId);
        preparedStatement.setLong(2, userId);
        preparedStatement.executeUpdate();
        connection.close();
    }

    static void unsetUserVote(long messageId, long userId) throws SQLException {
        var connection = Database.getConnection();
        var preparedStatement = connection.prepareStatement("DELETE FROM user_votes WHERE message_id=? AND user_id=?");
        preparedStatement.setLong(1, messageId);
        preparedStatement.setLong(2, userId);
        preparedStatement.executeUpdate();
        connection.close();
    }

    static boolean hasVoted(long messageId, long userId) throws SQLException {
        var connection = Database.getConnection();
        var preparedStatement = connection.prepareStatement("SELECT * FROM user_votes WHERE message_id=? AND user_id=?");
        preparedStatement.setLong(1, messageId);
        preparedStatement.setLong(2, userId);
        var resultSet = preparedStatement.executeQuery();
        boolean hasVoted = false;
        if (resultSet.first()) hasVoted = true;
        connection.close();
        return hasVoted;
    }
}
