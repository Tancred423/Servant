// Author: Tancred423 (https://github.com/Tancred423)
package plugins.moderation.bestOfQuote;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import servant.LoggingTask;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;

import static servant.Database.closeQuietly;

public class BestOfQuoteHandler {
    private final JDA jda;
    private final long guildId;

    public BestOfQuoteHandler(JDA jda, long guildId) {
        this.jda = jda;
        this.guildId = guildId;
    }

    public String getEmoji() {
        Connection connection = null;
        var emoji = "";

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT emoji FROM guild_best_of_quotes WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) emoji = resultSet.getString("emoji");
            if (emoji.isEmpty()) emoji = null;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "BestOfQuote#getEmoji"));
        } finally {
            closeQuietly(connection);
        }

        return emoji;
    }

    public TextChannel getChannel() {
        Connection connection = null;
        TextChannel channel = null;

        var guild = jda.getGuildById(guildId);
        if (guild == null) return null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT tc_id FROM guild_best_of_quotes WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) channel = guild.getTextChannelById(resultSet.getLong("tc_id"));
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "BestOfQuote#getChannel"));
        } finally {
            closeQuietly(connection);
        }

        if (channel != null && channel.getIdLong() == 0) channel = null;

        return channel;
    }

    public int getNumber() {
        Connection connection = null;
        int number = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT min_votes_flat FROM guild_best_of_quotes WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) number = resultSet.getInt("min_votes_flat");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "BestOfQuote#getNumber"));
        } finally {
            closeQuietly(connection);
        }

        return number;
    }

    public int getPercentage() {
        Connection connection = null;
        int percentage = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT min_votes_percent FROM guild_best_of_quotes WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) percentage = resultSet.getInt("min_votes_percent");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "BestOfQuote#getPercentage"));
        } finally {
            closeQuietly(connection);
        }

        return percentage;
    }

    public void addBlacklist(long msgId, long tcId) {
        Connection connection = null;

        try {
            if (!isBlacklisted(msgId)) {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement("INSERT INTO tmp_best_of_quote_bl (msg_id,guild_id,tc_id) VALUES (?,?,?)");
                insert.setLong(1, msgId);
                insert.setLong(2, guildId);
                insert.setLong(3, tcId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "BestOfQuote#addBlacklist"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean isBlacklisted(long msgId) {
        Connection connection = null;
        var isBlacklisted = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM tmp_best_of_quote_bl WHERE msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) isBlacklisted = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "BestOfQuote#isBlacklisted"));
        } finally {
            closeQuietly(connection);
        }

        return isBlacklisted;
    }
}