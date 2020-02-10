// Author: Tancred423 (https://github.com/Tancred423)
package moderation.bestOfQuote;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.TextChannel;
import servant.LoggingTask;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;

import static servant.Database.closeQuietly;

public class BestOfQuote {
    private JDA jda;
    private long guildId;

    public BestOfQuote(JDA jda, long guildId) {
        this.jda = jda;
        this.guildId = guildId;
    }

    private boolean hasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM best_of_quote WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new LoggingTask(e, jda, "BestOfQuote#hasEntry");
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public Emote getEmote() {
        Connection connection = null;
        Emote emote = null;

        var thisGuild = jda.getGuildById(guildId);
        if (thisGuild == null) return null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM best_of_quote WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                var guildId = resultSet.getLong("emote_guild_id");
                var emoteId = resultSet.getLong("emote_id");
                if (guildId != 0 && emoteId != 0) emote = thisGuild.getEmoteById(emoteId);
            }
        } catch (SQLException e) {
            new LoggingTask(e, jda, "BestOfQuote#getEmote");
        } finally {
            closeQuietly(connection);
        }

        return emote;
    }

    public String getEmoji() {
        Connection connection = null;
        var emoji = "";

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT emoji FROM best_of_quote WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) emoji = resultSet.getString("emoji");
            if (emoji.isEmpty()) emoji = null;
        } catch (SQLException e) {
            new LoggingTask(e, jda, "BestOfQuote#getEmoji");
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
            var select = connection.prepareStatement("SELECT channel_id FROM best_of_quote WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) channel = guild.getTextChannelById(resultSet.getLong("channel_id"));
        } catch (SQLException e) {
            new LoggingTask(e, jda, "BestOfQuote#getChannel");
        } finally {
            closeQuietly(connection);
        }

        return channel;
    }

    public int getNumber() {
        Connection connection = null;
        int number = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT number FROM best_of_quote WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) number = resultSet.getInt("number");
        } catch (SQLException e) {
            new LoggingTask(e, jda, "BestOfQuote#getNumber");
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
            var select = connection.prepareStatement("SELECT percentage FROM best_of_quote WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) percentage = resultSet.getInt("percentage");
        } catch (SQLException e) {
            new LoggingTask(e, jda, "BestOfQuote#getPercentage");
        } finally {
            closeQuietly(connection);
        }

        return percentage;
    }

    public void setChannel(long channelId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (hasEntry()) {
                var update = connection.prepareStatement("UPDATE best_of_quote SET channel_id=? WHERE guild_id=?");
                update.setLong(1, channelId);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO best_of_quote (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, channelId);
                insert.setInt(3, 0);
                insert.setInt(4, 0);
                insert.setString(5, "");
                insert.setLong(6, 0);
                insert.setLong(7, 0);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new LoggingTask(e, jda, "BestOfQuote#setChannel");
        } finally {
            closeQuietly(connection);
        }
    }

    public void setEmote(long emoteGuildId, long emoteId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (hasEntry()) {
                var update = connection.prepareStatement("UPDATE best_of_quote SET emoji=?, emote_guild_id=?, emote_id=? WHERE guild_id=?");
                update.setString(1, "");
                update.setLong(2, emoteGuildId);
                update.setLong(3, emoteId);
                update.setLong(4, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO best_of_quote (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, 0);
                insert.setInt(3, 0);
                insert.setInt(4, 0);
                insert.setString(5, "");
                insert.setLong(6, emoteGuildId);
                insert.setLong(7, emoteId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new LoggingTask(e, jda, "BestOfQuote#setEmote");
        } finally {
            closeQuietly(connection);
        }
    }

    public void setEmoji(String emoji) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (hasEntry()) {
                var update = connection.prepareStatement("UPDATE best_of_quote SET emoji=?, emote_guild_id=?, emote_id=? WHERE guild_id=?");
                update.setString(1, emoji);
                update.setLong(2, 0);
                update.setLong(3, 0);
                update.setLong(4, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO best_of_quote (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, 0);
                insert.setInt(3, 0);
                insert.setInt(4, 0);
                insert.setString(5, emoji);
                insert.setLong(6, 0);
                insert.setLong(7, 0);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new LoggingTask(e, jda, "BestOfQuote#setEmoji");
        } finally {
            closeQuietly(connection);
        }
    }

    public void setNumber(int number) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (hasEntry()) {
                var update = connection.prepareStatement("UPDATE best_of_quote SET number=? WHERE guild_id=?");
                update.setInt(1, number);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO best_of_quote (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, 0);
                insert.setInt(3, number);
                insert.setInt(4, 0);
                insert.setString(5, "");
                insert.setLong(6, 0);
                insert.setLong(7, 0);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new LoggingTask(e, jda, "BestOfQuote#setNumber");
        } finally {
            closeQuietly(connection);
        }
    }

    public void setPercentage(int percentage) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (hasEntry()) {
                var update = connection.prepareStatement("UPDATE best_of_quote SET percentage=? WHERE guild_id=?");
                update.setInt(1, percentage);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO best_of_quote (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, 0);
                insert.setInt(3, 0);
                insert.setInt(4, percentage);
                insert.setString(5, "");
                insert.setLong(6, 0);
                insert.setLong(7, 0);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new LoggingTask(e, jda, "BestOfQuote#setPercentage");
        } finally {
            closeQuietly(connection);
        }
    }

    public void addBlacklist(long messageId) {
        Connection connection = null;

        try {
            if (!isBlacklisted(messageId)) {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement("INSERT INTO best_of_quote_bl (message_id) VALUES (?)");
                insert.setLong(1, messageId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new LoggingTask(e, jda, "BestOfQuote#addBlacklist");
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean isBlacklisted(long messageId) {
        Connection connection = null;
        var isBlacklisted = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM best_of_quote_bl WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isBlacklisted = true;
        } catch (SQLException e) {
            new LoggingTask(e, jda, "BestOfQuote#isBlacklisted");
        } finally {
            closeQuietly(connection);
        }

        return isBlacklisted;
    }
}
