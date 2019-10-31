// Author: Tancred423 (https://github.com/Tancred423)
package moderation.guild;

import net.dv8tion.jda.core.entities.*;
import servant.Log;
import servant.Servant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static utilities.DatabaseConn.*;

public class Guild {
    private long guildId;

    public Guild(long guildId) {
        this.guildId = guildId;
    }

    // Signup
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isSignupMessage(long messageId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var isSignupMessage = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT message_id FROM signup WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            isSignupMessage = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "signup", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isSignupMessage;
    }

    public long getSignupAuthorId(long messageId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var authorId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT author_id FROM signup WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) authorId = resultSet.getLong("author_id");
        } catch (SQLException e) {
            new Log(e, guild, user, "signup", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return authorId;
    }

    public int getSignupAmount(long messageId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var amount = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT amount FROM signup WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) amount = resultSet.getInt("amount");
        } catch (SQLException e) {
            new Log(e, guild, user, "signup", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return amount;
    }

    public String getSignupTitle(long messageId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        String authorId = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT title FROM signup WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) authorId = resultSet.getString("title");
        } catch (SQLException e) {
            new Log(e, guild, user, "signup", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return authorId;
    }

    public void setSignup(long messageId, long authorId, int amount, String title, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("INSERT INTO signup (message_id,author_id,amount,title) VALUES (?,?,?,?)");
            insert.setLong(1, messageId);
            insert.setLong(2, authorId);
            insert.setInt(3, amount);
            insert.setString(4, title);
            insert.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "signup", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetSignup(long messageId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM signup WHERE message_id=?");
            delete.setLong(1, messageId);
            delete.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "signup", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // BestOfQuote
    private boolean bestOfQuoteHasEntry(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM best_of_quote WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "bestofquote", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public Emote getBestOfQuoteEmote(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        Emote emote = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM best_of_quote WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                var guildId = resultSet.getLong("emote_guild_id");
                var emoteId = resultSet.getLong("emote_id");
                if (guildId != 0 && emoteId != 0) emote = Servant.jda.getGuildById(guildId).getEmoteById(emoteId);
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "bestofquote", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return emote;
    }

    public String getBestOfQuoteEmoji(net.dv8tion.jda.core.entities.Guild guild, User user) {
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
            new Log(e, guild, user, "bestofquote", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return emoji;
    }

    public TextChannel getBestOfQuoteChannel(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        TextChannel channel = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT channel_id FROM best_of_quote WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) channel = Servant.jda.getGuildById(guildId).getTextChannelById(resultSet.getLong("channel_id"));
        } catch (SQLException e) {
            new Log(e, guild, user, "bestofquote", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return channel;
    }

    public int getBestOfQuoteNumber(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        int number = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT number FROM best_of_quote WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) number = resultSet.getInt("number");
        } catch (SQLException e) {
            new Log(e, guild, user, "bestofquote", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return number;
    }

    public int getBestOfQuotePercentage(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        int percentage = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT percentage FROM best_of_quote WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) percentage = resultSet.getInt("percentage");
        } catch (SQLException e) {
            new Log(e, guild, user, "bestofquote", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return percentage;
    }

    public void setBestOfQuoteChannel(long channelId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bestOfQuoteHasEntry(guild, user)) {
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
            new Log(e, guild, user, "bestofquote", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void setBestOfQuoteEmote(long emoteGuildId, long emoteId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bestOfQuoteHasEntry(guild, user)) {
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
            new Log(e, guild, user, "bestofquote", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void setBestOfQuoteEmoji(String emoji, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bestOfQuoteHasEntry(guild, user)) {
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
            new Log(e, guild, user, "bestofquote", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void setBestOfQuoteNumber(int number, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bestOfQuoteHasEntry(guild, user)) {
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
            new Log(e, guild, user, "bestofquote", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void setBestOfQuotePercentage(int percentage, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bestOfQuoteHasEntry(guild, user)) {
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
            new Log(e, guild, user, "bestofquote", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void addBestOfQuoteBlacklist(long messageId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            if (!bestOfQuoteIsBlacklisted(messageId, guild, user)) {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement("INSERT INTO best_of_quote_bl (message_id) VALUES (?)");
                insert.setLong(1, messageId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "bestofquote", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean bestOfQuoteIsBlacklisted(long messageId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var isBlacklisted = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM best_of_quote_bl WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isBlacklisted = true;
        } catch (SQLException e) {
            new Log(e, guild, user, "bestofquote", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isBlacklisted;
    }

    // BestOfImage
    private boolean bestOfImageHasEntry(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            PreparedStatement select = connection.prepareStatement("SELECT * FROM best_of_image WHERE guild_id=?");
            select.setLong(1, guildId);
            ResultSet resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "bestofimage", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public Emote getBestOfImageEmote(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        Emote emote = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM best_of_image WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                var guildId = resultSet.getLong("emote_guild_id");
                var emoteId = resultSet.getLong("emote_id");
                if (guildId != 0 && emoteId != 0) emote = Servant.jda.getGuildById(guildId).getEmoteById(emoteId);
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "bestofimage", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return emote;
    }

    public String getBestOfImageEmoji(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var emoji = "";

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT emoji FROM best_of_image WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) emoji = resultSet.getString("emoji");
            if (emoji.isEmpty()) emoji = null;
        } catch (SQLException e) {
            new Log(e, guild, user, "bestofimage", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return emoji;
    }

    public TextChannel getBestOfImageChannel(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        TextChannel channel = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT channel_id FROM best_of_image WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) channel = Servant.jda.getGuildById(guildId).getTextChannelById(resultSet.getLong("channel_id"));
        } catch (SQLException e) {
            new Log(e, guild, user, "bestofimage", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return channel;
    }

    public int getBestOfImageNumber(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        int number = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT number FROM best_of_image WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) number = resultSet.getInt("number");
        } catch (SQLException e) {
            new Log(e, guild, user, "bestofimage", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return number;
    }

    public int getBestOfImagePercentage(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        int percentage = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT percentage FROM best_of_image WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) percentage = resultSet.getInt("percentage");
        } catch (SQLException e) {
            new Log(e, guild, user, "bestofimage", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return percentage;
    }

    public void setBestOfImageChannel(long channelId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bestOfImageHasEntry(guild, user)) {
                var update = connection.prepareStatement("UPDATE best_of_image SET channel_id=? WHERE guild_id=?");
                update.setLong(1, channelId);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO best_of_image (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
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
            new Log(e, guild, user, "bestofimage", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void setBestOfImageEmote(long emoteGuildId, long emoteId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bestOfImageHasEntry(guild, user)) {
                var update = connection.prepareStatement("UPDATE best_of_image SET emoji=?, emote_guild_id=?, emote_id=? WHERE guild_id=?");
                update.setString(1, "");
                update.setLong(2, emoteGuildId);
                update.setLong(3, emoteId);
                update.setLong(4, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO best_of_image (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
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
            new Log(e, guild, user, "bestofimage", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void setBestOfImageEmoji(String emoji, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bestOfImageHasEntry(guild, user)) {
                var update = connection.prepareStatement("UPDATE best_of_image SET emoji=?, emote_guild_id=?, emote_id=? WHERE guild_id=?");
                update.setString(1, emoji);
                update.setLong(2, 0);
                update.setLong(3, 0);
                update.setLong(4, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO best_of_image (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
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
            new Log(e, guild, user, "bestofimage", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void setBestOfImageNumber(int number, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bestOfImageHasEntry(guild, user)) {
                var update = connection.prepareStatement("UPDATE best_of_image SET number=? WHERE guild_id=?");
                update.setInt(1, number);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO best_of_image (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
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
            new Log(e, guild, user, "bestofimage", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void setBestOfImagePercentage(int percentage, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bestOfImageHasEntry(guild, user)) {
                var update = connection.prepareStatement("UPDATE best_of_image SET percentage=? WHERE guild_id=?");
                update.setInt(1, percentage);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO best_of_image (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
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
            new Log(e, guild, user, "bestofimage", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void addBestOfImageBlacklist(long messageId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (!bestOfImageIsBlacklisted(messageId, guild, user)) {
                var insert = connection.prepareStatement("INSERT INTO best_of_image_bl (message_id) VALUES (?)");
                insert.setLong(1, messageId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "bestofimage", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean bestOfImageIsBlacklisted(long messageId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var isBlacklisted = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM best_of_image_bl WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isBlacklisted = true;
        } catch (SQLException e) {
            new Log(e, guild, user, "bestofimage", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isBlacklisted;
    }

    // Birthday
    public boolean wasGratulated(long userId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var wasGratulated = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT was_gratulated FROM birthday_gratulation WHERE guild_id=? and user_id=?");
            select.setLong(1, guildId);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) wasGratulated = resultSet.getBoolean("was_gratulated");
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasGratulated;
    }

    private boolean birthdayGratulationHasEntry(long userId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM birthday_gratulation WHERE guild_id=? and user_id=?");
            select.setLong(1, guildId);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public void setGratulated(long userId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            if (!birthdayGratulationHasEntry(userId, guild, user)) {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement("INSERT INTO birthday_gratulation (guild_id,user_id,was_gratulated) VALUES (?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, userId);
                insert.setBoolean(3, true);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetGratulated(long userId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            if (birthdayGratulationHasEntry(userId, guild, user)) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement("DELETE FROM birthday_gratulation WHERE guild_id=? AND user_id=?");
                delete.setLong(1, guildId);
                delete.setLong(2, userId);
                delete.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    private void unsetGratulateds(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM birthday_gratulation WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public long getBirthdayMessageChannelId(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var channelId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT channel_id FROM birthday_messages WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) channelId = resultSet.getLong("channel_id");
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return channelId;
    }

    public long getBirthdayMessageMessageId(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var messageId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT message_id FROM birthday_messages WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) messageId = resultSet.getLong("message_id");
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return messageId;
    }

    public long getBirthdayMessageAuthorId(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var messageId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT user_id FROM birthday_messages WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) messageId = resultSet.getLong("user_id");
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return messageId;
    }

    public boolean birthdayMessagesHasEntry(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM birthday_messages WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public void setBirthdayMessage(long channelId, long messageId, long userId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (birthdayMessagesHasEntry(guild, user)) {
                var update = connection.prepareStatement("UPDATE birthday_messages SET channel_id=?, message_id=?, user_id=? WHERE guild_id=?");
                update.setLong(1, channelId);
                update.setLong(2, messageId);
                update.setLong(3, userId);
                update.setLong(4, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO birthday_messages (guild_id,channel_id,message_id,user_id) VALUES (?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, channelId);
                insert.setLong(3, messageId);
                insert.setLong(4, userId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetBirthdayMessage(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            if (birthdayMessagesHasEntry(guild, user)) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement("DELETE FROM birthday_messages WHERE guild_id=?");
                delete.setLong(1, guildId);
                delete.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void purgeBirthday(net.dv8tion.jda.core.entities.Guild guild, User user) {
        unsetBirthdayMessage(guild, user);
        unsetBirthdayChannelId(guild, user);
        unsetBirthdays(guild, user);
        unsetGratulateds(guild, user);
    }

    public Map<Long, String> getBirthdays(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var birthdays = new HashMap<Long, String>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM birthdays WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do birthdays.put(resultSet.getLong("user_id"), resultSet.getString("birthday"));
                while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return birthdays;
    }

    private boolean birthdaysHasEntry(long userId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT birthday FROM birthdays WHERE guild_id=? AND user_id=?");
            select.setLong(1, guildId);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public void setBirthday(long userId, String birthday, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (birthdaysHasEntry(userId, guild, user)) {
                var update = connection.prepareStatement("UPDATE birthdays SET birthday=? WHERE guild_id=? AND user_id=?");
                update.setString(1, birthday);
                update.setLong(2, guildId);
                update.setLong(3, userId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO birthdays (guild_id,user_id,birthday) VALUES (?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, userId);
                insert.setString(3, birthday);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean unsetBirthday(long userId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var wasUnset = false;

        try {
            if (birthdaysHasEntry(userId, guild, user)) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement("DELETE FROM birthdays WHERE guild_id=? AND user_id=?");
                delete.setLong(1, guildId);
                delete.setLong(2, userId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    private void unsetBirthdays(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM birthdays WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public long getBirthdayChannelId(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var birthdayChannelId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT birthday_channel_id FROM guild WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) birthdayChannelId = resultSet.getLong("birthday_channel_id");
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return birthdayChannelId;
    }

    public void setBirthdayChannelId(long birthdayChannelId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (guildHasEntry(guild, user)) {
                var update = connection.prepareStatement("UPDATE guild SET birthday_channel_id=? WHERE guild_id=?");
                update.setLong(1, birthdayChannelId);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO guild (guild_id,prefix,offset,language,birthday_channel_id) VALUES (?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setString(2, Servant.config.getDefaultPrefix());
                insert.setString(3, "Z");
                insert.setString(4, Servant.config.getDefaultLanguage());
                insert.setLong(5, birthdayChannelId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "birthday", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetBirthdayChannelId(net.dv8tion.jda.core.entities.Guild guild, User user) {
        setBirthdayChannelId(0L, guild, user);
    }

    // Guild
    private boolean guildHasEntry(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM guild WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "guild", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    // Level Role
    public List<Long> getLevelRolesForLevel(int level, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        List<Long> roleId = new ArrayList<>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM level_role WHERE guild_id=? ORDER BY level DESC");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do if (level >= resultSet.getInt("level")) roleId.add(resultSet.getLong("role_id"));
                while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return roleId;
    }

    public List<Long> getLevelRole(int level, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        List<Long> roleId = new ArrayList<>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT role_id FROM level_role WHERE guild_id=? AND level=?");
            select.setLong(1, guildId);
            select.setInt(2, level);
            var resultSet = select.executeQuery();
            if (resultSet.first()) do roleId.add(resultSet.getLong("role_id")); while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return roleId;
    }

    public Map<Integer, Long> getLevelRoles(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        Map<Integer, Long> levelRoles = new HashMap<>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM level_role WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do levelRoles.put(resultSet.getInt("level"), resultSet.getLong("role_id"));
                while(resultSet.next());
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return levelRoles;
    }

    public boolean setLevelRole(int level, long roleId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var wasSet = false;

        try {
            if (!getLevelRole(level, guild, user).contains(roleId)) {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement("INSERT INTO level_role (guild_id,level,role_id) VALUES (?,?,?)");
                insert.setLong(1, guildId);
                insert.setInt(2, level);
                insert.setLong(3, roleId);
                insert.executeUpdate();
                wasSet = true;
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasSet;
    }

    public void unsetLevelRole(int level, long roleId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM level_role WHERE guild_id=? AND level=? AND role_id=?");
            delete.setLong(1, guildId);
            delete.setInt(2, level);
            delete.setLong(3, roleId);
            delete.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Language
    public String getLanguage(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        String language = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT language FROM guild WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) language = resultSet.getString("language");
            if (language == null) language = Servant.config.getDefaultLanguage();
            else if (language.isEmpty()) language = Servant.config.getDefaultLanguage();
        } catch (SQLException e) {
            new Log(e, guild, user, "language", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return language;
    }

    public void setLanguage(String language, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (guildHasEntry(guild, user)) {
                var update = connection.prepareStatement("UPDATE guild SET language=? WHERE guild_id=?");
                update.setString(1, language);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO guild (guild_id,prefix,offset,language,birthday_channel_id) VALUES (?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setString(2, Servant.config.getDefaultPrefix());
                insert.setString(3, Servant.config.getDefaultOffset());
                insert.setString(4, language);
                insert.setLong(5, 0);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "language", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    void unsetLanguage(net.dv8tion.jda.core.entities.Guild guild, User user) {
        setLanguage(Servant.config.getDefaultLanguage(), guild, user);
    }

    // Lobby.
    private boolean lobbyHasEntry(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM lobby WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "lobby", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public List<Long> getLobbies(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var lobbies = new ArrayList<Long>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM lobby WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) do lobbies.add(resultSet.getLong("channel_id")); while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, guild, user, "lobby", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return lobbies;
    }

    public void setLobby(long channelId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("INSERT INTO lobby (guild_id,channel_id) VALUES (?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, channelId);
            insert.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "lobby", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean unsetLobby(long channelId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var wasUnset = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (lobbyHasEntry(guild, user)) {
                var delete = connection.prepareStatement("DELETE FROM lobby WHERE guild_id=? and channel_id=?");
                delete.setLong(1, guildId);
                delete.setLong(2, channelId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "lobby", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    // Prefix.
    public String getPrefix(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var prefix = Servant.config.getDefaultPrefix();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT prefix FROM guild WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) prefix = resultSet.getString("prefix");
        } catch (SQLException e) {
            new Log(e, guild, user, "prefix", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return prefix;
    }


    public void setPrefix(String prefix, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (guildHasEntry(guild, user)) {
                var update = connection.prepareStatement("UPDATE guild SET prefix=? WHERE guild_id=?");
                update.setString(1, prefix);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO guild (guild_id,prefix,offset,language,birthday_channel_id) VALUES (?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setString(2, prefix);
                insert.setString(3, Servant.config.getDefaultOffset());
                insert.setString(4, Servant.config.getDefaultLanguage());
                insert.setLong(5, 0L);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "prefix", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    void unsetPrefix(net.dv8tion.jda.core.entities.Guild guild, User user) {
        setPrefix(Servant.config.getDefaultPrefix(), guild, user);
    }

    // Offset
    public String getOffset(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var offset = Servant.config.getDefaultOffset();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT offset FROM guild WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) offset = resultSet.getString("offset");
            offset = offset.equals("00:00") ? "Z" : offset;
        } catch (SQLException e) {
            new Log(e, guild, user, "offset", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return offset;
    }

    public void setOffset(String offset, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (guildHasEntry(guild, user)) {
                var update = connection.prepareStatement("UPDATE guild SET offset=? WHERE guild_id=?");
                update.setString(1, offset);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO guild (guild_id,prefix,offset,language,birthday_channel_id) VALUES (?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setString(2, Servant.config.getDefaultPrefix());
                insert.setString(3, offset);
                insert.setString(4, Servant.config.getDefaultLanguage());
                insert.setLong(5, 0L);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "offset", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    void unsetOffset(net.dv8tion.jda.core.entities.Guild guild, User user) {
        setOffset(Servant.config.getDefaultOffset(), guild, user);
    }

    // Featurecount
    private boolean featureCountHasEntry(String key, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM feature_count WHERE id=? AND feature=?");
            select.setLong(1, guildId);
            select.setString(2, key);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "featurecount", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    private int getFeatureCount(String feature, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var featureCount = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT count FROM feature_count WHERE id=? AND feature=?");
            select.setLong(1, guildId);
            select.setString(2, feature.toLowerCase());
            var resultSet = select.executeQuery();
            if (resultSet.first()) featureCount = resultSet.getInt("count");
        } catch (SQLException e) {
            new Log(e, guild, user, "featurecount", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return featureCount;
    }

    public void incrementFeatureCount(String feature, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (featureCountHasEntry(feature, guild, user)) {
                var count = getFeatureCount(feature, guild, user);
                var update = connection.prepareStatement("UPDATE feature_count SET count=? WHERE id=? AND feature=?");
                update.setInt(1, count + 1);
                update.setLong(2, guildId);
                update.setString(3, feature.toLowerCase());
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO feature_count (id,feature,count) VALUES (?,?,?)");
                insert.setLong(1, guildId);
                insert.setString(2, feature.toLowerCase());
                insert.setInt(3, 1);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "featurecount", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Level
    public Map<Long, Integer> getLeaderboard(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var userExp = new LinkedHashMap<Long, Integer>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM user_exp WHERE guild_id=? ORDER BY exp DESC");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                var counter = 0;
                do {
                    if (counter >= 10) break;
                    userExp.put(resultSet.getLong("user_id"), resultSet.getInt("exp"));
                    counter++;
                } while (resultSet.next());
            }
            if (userExp.isEmpty()) userExp = null;
        } catch (SQLException e) {
            new Log(e, guild, user, "level", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return userExp;
    }

    // Autorole
    public boolean hasAutorole(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasAutorole = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM autorole WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasAutorole = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "autorole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasAutorole;
    }

    public Map<Role, Integer> getAutorole(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var role = new HashMap<Role, Integer>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM autorole WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                var roleId = resultSet.getLong("role_id");
                var delay = resultSet.getInt("delay");
                role.put(Servant.jda.getGuildById(guildId).getRoleById(roleId), delay);
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "autorole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return role;
    }

    private boolean autoroleHasEntry(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM autorole WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "autorole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public void setAutorole(long roleId, int delay, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (autoroleHasEntry(guild, user)) {
                var update = connection.prepareStatement("UPDATE autorole SET role_id=?, delay=? WHERE guild_id=?");
                update.setLong(1, roleId);
                update.setInt(2, delay);
                update.setLong(3, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO autorole (guild_id,role_id,delay) VALUES (?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, roleId);
                insert.setInt(3, delay);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "autorole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean unsetAutorole(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var wasUnset = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (autoroleHasEntry(guild, user)) {
                var delete = connection.prepareStatement("DELETE FROM autorole WHERE guild_id=?");
                delete.setLong(1, guildId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "autorole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    // MediaOnlyChannel
    public boolean mediaOnlyChannelHasEntry(MessageChannel channel, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM mediaonlychannel WHERE guild_id=? AND channel_id=?");
            select.setLong(1, guildId);
            select.setLong(2, channel.getIdLong());
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "mediaonlychannel", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public boolean setMediaOnlyChannel(MessageChannel channel, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var wasSet = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (!mediaOnlyChannelHasEntry(channel, guild, user)) {
                var insert = connection.prepareStatement("INSERT INTO mediaonlychannel (guild_id,channel_id) VALUES (?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, channel.getIdLong());
                insert.executeUpdate();
                wasSet = true;
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "mediaonlychannel", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasSet;
    }

    public boolean unsetMediaOnlyChannel(MessageChannel channel, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var wasUnset = false;

        try {
            if (mediaOnlyChannelHasEntry(channel, guild, user)) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement("DELETE FROM mediaonlychannel WHERE guild_id=? AND channel_id=?");
                delete.setLong(1, guildId);
                delete.setLong(2, channel.getIdLong());
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "mediaonlychannel", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    public void unsetMediaOnlyChannels(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM mediaonlychannel WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "mediaonlychannel", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public List<MessageChannel> getMediaOnlyChannels(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var channels = new ArrayList<MessageChannel>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM mediaonlychannel WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do channels.add(Servant.jda.getGuildById(guildId).getTextChannelById(resultSet.getLong("channel_id")));
                while (resultSet.next());
            }
            if (channels.isEmpty()) channels = null;
        } catch (SQLException e) {
            new Log(e, guild, user, "mediaonlychannel", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return channels;
    }

    // Toggle
    private boolean toggleHasEntry(String feature, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM toggle WHERE guild_id=? AND feature=?");
            select.setLong(1, guildId);
            select.setString(2, feature);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "toggle", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public boolean getToggleStatus(String feature, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var isEnabled = true;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT is_enabled FROM toggle WHERE guild_id=? AND feature=?");
            select.setLong(1, guildId);
            select.setString(2, feature);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isEnabled = resultSet.getBoolean("is_enabled");
            else isEnabled = Servant.toggle.get(feature);
        } catch (SQLException e) {
            new Log(e, guild, user, "toggle", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isEnabled;
    }

    public void setToggleStatus(String feature, boolean status, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (toggleHasEntry(feature, guild, user)) {
                var update = connection.prepareStatement("UPDATE toggle SET is_enabled=? WHERE guild_id=? AND feature=?");
                update.setBoolean(1, status);
                update.setLong(2, guildId);
                update.setString(3, feature);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO toggle (guild_id,feature,is_enabled) VALUES (?,?,?)");
                insert.setLong(1, guildId);
                insert.setString(2, feature);
                insert.setBoolean(3, status);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "toggle", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Join
    private boolean joinNotifierHasEntry(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM join_notifier WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "join", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public MessageChannel getJoinNotifierChannel(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        MessageChannel channel = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT channel_id FROM join_notifier WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) channel = Servant.jda.getGuildById(guildId).getTextChannelById(resultSet.getLong("channel_id"));
        } catch (SQLException e) {
            new Log(e, guild, user, "join", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return channel;
    }

    public void setJoinNotifierChannel(MessageChannel channel, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (joinNotifierHasEntry(guild, user)) {
                var update = connection.prepareStatement("UPDATE join_notifier SET channel_id=? WHERE guild_id=?");
                update.setLong(1, channel.getIdLong());
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO join_notifier (guild_id,channel_id) VALUES (?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, channel.getIdLong());
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "join", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean unsetJoinNotifierChannel(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var wasUnset = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (joinNotifierHasEntry(guild, user)) {
                var delete = connection.prepareStatement("DELETE FROM join_notifier WHERE guild_id=?");
                delete.setLong(1, guildId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "join", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    // Leave
    private boolean leaveNotifierHasEntry(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM leave_notifier WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "leave", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public MessageChannel getLeaveNotifierChannel(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        MessageChannel channel = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT channel_id FROM leave_notifier WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) channel = Servant.jda.getGuildById(guildId).getTextChannelById(resultSet.getLong("channel_id"));
        } catch (SQLException e) {
            new Log(e, guild, user, "leave", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return channel;
    }

    public void setLeaveNotifierChannel(MessageChannel channel, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (leaveNotifierHasEntry(guild, user)) {
                var update = connection.prepareStatement("UPDATE leave_notifier SET channel_id=? WHERE guild_id=?");
                update.setLong(1, channel.getIdLong());
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO leave_notifier (guild_id,channel_id) VALUES (?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, channel.getIdLong());
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "leave", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean unsetLeaveNotifierChannel(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var wasUnset = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (leaveNotifierHasEntry(guild, user)) {
                var delete = connection.prepareStatement("DELETE FROM leave_notifier WHERE guild_id=?");
                delete.setLong(1, guildId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "leave", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    // Exp
    public int getUserRank(long userId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        int rank = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM user_exp WHERE guild_id=? ORDER BY exp DESC");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                rank = 1;
                do {
                    if (resultSet.getLong("user_id") == userId) break;
                    else rank++;
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "level", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return rank;
    }

    // Livestream
    private boolean streamerModeHasEntry(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM streamer_mode WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public boolean isStreamerMode(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var isStreamerMode = true;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT is_streamer_mode FROM streamer_mode WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isStreamerMode = resultSet.getBoolean("is_streamer_mode");
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isStreamerMode;
    }

    public void toggleStreamerMode(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (streamerModeHasEntry(guild, user)) {
                var update = connection.prepareStatement("UPDATE streamer_mode SET is_streamer_mode=? WHERE guild_id=?");
                update.setBoolean(1, !isStreamerMode(guild, user));
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO streamer_mode (guild_id,is_streamer_mode) VALUES (?,?)");
                insert.setLong(1, guildId);
                insert.setBoolean(2, !isStreamerMode(guild, user));
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    private boolean streamChannelHasEntry(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM stream_channel WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public long getStreamChannelId(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var channelId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT channel_id FROM stream_channel WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) channelId = resultSet.getLong("channel_id");
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return channelId;
    }

    public void setStreamChannel(MessageChannel channel, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (streamChannelHasEntry(guild, user)) {
                var update = connection.prepareStatement("UPDATE stream_channel SET channel_id=? WHERE guild_id=?");
                update.setLong(1, channel.getIdLong());
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO stream_channel (guild_id,channel_id) VALUES (?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, channel.getIdLong());
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean unsetStreamChannel(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var wasUnset = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (streamChannelHasEntry(guild, user)) {
                var delete = connection.prepareStatement("DELETE FROM stream_channel WHERE guild_id=?");
                delete.setLong(1, guildId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    private boolean streamingRoleHasEntry(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM streaming_role WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public long getStreamingRoleId(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var role = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT role_id FROM streaming_role WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) role = resultSet.getLong("role_id");
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return role;
    }

    public void setStreamingRole(long roleId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (streamingRoleHasEntry(guild, user)) {
                var update = connection.prepareStatement("UPDATE streaming_role SET role_id=? WHERE guild_id=?");
                update.setLong(1, roleId);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO streaming_role (guild_id,role_id) VALUES (?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, roleId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean unsetStreamingRole(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var wasUnset = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (streamingRoleHasEntry(guild, user)) {
                var delete = connection.prepareStatement("DELETE FROM streaming_role WHERE guild_id=?");
                delete.setLong(1, guildId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    private boolean isStreamer(long userId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var isStreamer = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM streamers WHERE guild_id=? AND user_id=?");
            select.setLong(1, guildId);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isStreamer = true;
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isStreamer;
    }

    public List<Long> getStreamers(net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var streamers = new ArrayList<Long>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT user_id FROM streamers WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) do streamers.add(resultSet.getLong("user_id")); while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return streamers;
    }

    public void setStreamer(long userId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("INSERT INTO streamers (guild_id,user_id) VALUES (?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, userId);
            insert.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean unsetStreamer(long userId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var wasUnset = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (isStreamer(userId, guild, user)) {
                var delete = connection.prepareStatement("DELETE FROM streamers WHERE guild_id=? AND user_id=?");
                delete.setLong(1, guildId);
                delete.setLong(2, userId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    // Reaction Role
    public boolean setReactionRole(long guildId, long channelId, long messageId, String emoji, long emoteGuildId,
                               long emoteId, long roleId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var wasSet = true;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (!reactionRoleHasEntry(guildId, channelId, messageId, emoji, emoteGuildId, emoteId, guild, user)) {
                var insert = connection.prepareStatement("INSERT INTO reaction_role (guild_id, channel_id, message_id, emoji, emote_guild_id, emote_id, role_id) VALUES (?,?,?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, channelId);
                insert.setLong(3, messageId);
                insert.setString(4, (emoji == null ? "" : emoji));
                insert.setLong(5, emoteGuildId);
                insert.setLong(6, emoteId);
                insert.setLong(7, roleId);
                insert.executeUpdate();
                wasSet = false;
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "reactionrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasSet;
    }

    public boolean unsetReactionRole(long guildId, long channelId, long messageId, String emoji, long emoteGuildId,
                                 long emoteId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var wasSet = true;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (reactionRoleHasEntry(guildId, channelId, messageId, emoji, emoteGuildId, emoteId, guild, user)) {
                var delete = connection.prepareStatement("DELETE FROM reaction_role WHERE guild_id=? AND channel_id=? AND message_id=? AND emoji=? AND emote_guild_id=? AND emote_id=?");
                delete.setLong(1, guildId);
                delete.setLong(2, channelId);
                delete.setLong(3, messageId);
                delete.setString(4, (emoji == null ? "" : emoji));
                delete.setLong(5, emoteGuildId);
                delete.setLong(6, emoteId);
                delete.executeUpdate();
                wasSet = false;
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "reactionrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasSet;
    }

    public long getRoleId(long guildId, long channelId, long messageId, String emoji, long emoteGuildId,
                          long emoteId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var roleId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT role_id FROM reaction_role WHERE guild_id=? AND channel_id=? AND message_id=? AND emoji=? AND emote_guild_id=? AND emote_id=?");
            select.setLong(1, guildId);
            select.setLong(2, channelId);
            select.setLong(3, messageId);
            select.setString(4, (emoji == null ? "" : emoji));
            select.setLong(5, emoteGuildId);
            select.setLong(6, emoteId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) roleId = resultSet.getLong("role_id");
        } catch (SQLException e) {
            new Log(e, guild, user, "reactionrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return roleId;
    }

    public boolean reactionRoleHasEntry(long guildId, long channelId, long messageId, String emoji, long emoteGuildId,
                                        long emoteId, net.dv8tion.jda.core.entities.Guild guild, User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT role_id FROM reaction_role WHERE guild_id=? AND channel_id=? AND message_id=? AND emoji=? AND emote_guild_id=? AND emote_id=?");
            select.setLong(1, guildId);
            select.setLong(2, channelId);
            select.setLong(3, messageId);
            select.setString(4, (emoji == null ? "" : emoji));
            select.setLong(5, emoteGuildId);
            select.setLong(6, emoteId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "reactionrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }
}
