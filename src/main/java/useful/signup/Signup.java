package useful.signup;

import files.language.LanguageHandler;
import moderation.guild.Server;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import servant.LoggingTask;
import servant.Servant;
import useful.giveaway.GiveawayHandler;
import utilities.EmoteUtil;
import utilities.ImageUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static servant.Database.closeQuietly;

public class Signup {
    private JDA jda;
    private long messageId;

    public Signup(JDA jda, long messageId) {
        this.jda = jda;
        this.messageId = messageId;
    }

    public int getAmount() {
        Connection connection = null;
        var amount = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT amount FROM signup WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) amount = resultSet.getInt("amount");
        } catch (SQLException e) {
            new LoggingTask(e, jda, "Signup#getAmount");
        } finally {
            closeQuietly(connection);
        }

        return amount;
    }

    public String getTitle() {
        Connection connection = null;
        String authorId = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT title FROM signup WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) authorId = resultSet.getString("title");
        } catch (SQLException e) {
            new LoggingTask(e, jda, "Signup#getTitle");
        } finally {
            closeQuietly(connection);
        }

        return authorId;
    }

    public boolean isSignup() {
        Connection connection = null;
        var isSignupMessage = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT message_id FROM signup WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            isSignupMessage = resultSet.first();
        } catch (SQLException e) {
            new LoggingTask(e, jda, "Signup#isSignup");
        } finally {
            closeQuietly(connection);
        }

        return isSignupMessage;
    }

    public long getAuthorId() {
        Connection connection = null;
        var authorId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT author_id FROM signup WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) authorId = resultSet.getLong("author_id");
        } catch (SQLException e) {
            new LoggingTask(e, jda, "Signup#getAuthorId");
        } finally {
            closeQuietly(connection);
        }

        return authorId;
    }

    public Timestamp getTime() {
        Connection connection = null;
        Timestamp expiration = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT time FROM signup WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) expiration = resultSet.getTimestamp("time");
        } catch (SQLException e) {
            new LoggingTask(e, jda, "Signup#getTime");
        } finally {
            closeQuietly(connection);
        }

        return expiration;
    }

    public boolean isCustomDate() {
        Connection connection = null;
        var isCustomDate = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT is_custom_date FROM signup WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isCustomDate = resultSet.getBoolean("is_custom_date");
        } catch (SQLException e) {
            new LoggingTask(e, jda, "Signup#isCustomDate");
        } finally {
            closeQuietly(connection);
        }

        return isCustomDate;
    }

    public void set(long authorId, int amount, String title, Timestamp time, long guildId, long channelId, boolean isCustomDate) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("INSERT INTO signup (message_id,author_id,amount,title,time,guild_id,channel_id,is_custom_date) VALUES (?,?,?,?,?,?,?,?)");
            insert.setLong(1, messageId);
            insert.setLong(2, authorId);
            insert.setInt(3, amount);
            insert.setString(4, title);
            insert.setTimestamp(5, time);
            insert.setLong(6, guildId);
            insert.setLong(7, channelId);
            insert.setBoolean(8, isCustomDate);
            insert.executeUpdate();
        } catch (SQLException e) {
            new LoggingTask(e, jda, "Signup#set");
        } finally {
            closeQuietly(connection);
        }
    }

    public void unset() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM signup WHERE message_id=?");
            delete.setLong(1, messageId);
            delete.executeUpdate();
        } catch (SQLException e) {
            new LoggingTask(e, jda, "Signup#unset");
        } finally {
            closeQuietly(connection);
        }
    }


    public static void check(JDA jda) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM signup");
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do {
                    var guildId = resultSet.getLong("guild_id");
                    if (jda.getGuildById(guildId) == null) {
                        var delete = connection.prepareStatement("DELETE FROM signup WHERE guild_id=?");
                        delete.setLong(1, guildId);
                        delete.executeUpdate();
                    } else {
                        var guild = jda.getGuildById(guildId);
                        if (guild == null) return;
                        var expiration = resultSet.getTimestamp("time").toLocalDateTime()
                                .atZone(ZoneId.of(new Server(guild).getOffset()));
                        var isCustomDate = resultSet.getBoolean("is_custom_date");
                        if (isCustomDate) expiration = expiration.minusMinutes(30);
                        var now = ZonedDateTime.now(ZoneOffset.of(new Server(guild).getOffset()));
                        var remainingTimeMillis = GiveawayHandler.zonedDateTimeDifference(now, expiration);

                        if (remainingTimeMillis <= 0) {
                            var server = new Server(guild);
                            var messageId = resultSet.getLong("message_id");
                            var finalExpiration = isCustomDate ? expiration.plusMinutes(30) : expiration;
                            var tc = guild.getTextChannelById(resultSet.getLong("channel_id"));
                            if (tc != null) tc.retrieveMessageById(messageId).queue(message -> {
                                var signup = new Signup(jda, messageId);
                                signup.end(server, message, true, finalExpiration.toInstant());
                            });
                        }
                    }
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            new LoggingTask(e, jda, "Signup#check");
        } finally {
            closeQuietly(connection);
        }
    }

    public void end(Server server, Message message, boolean forceEnd, Instant expiration) {
        var signup = new Signup(jda, messageId);
        if (!signup.isSignup()) return;
        var amount = signup.getAmount();
        var reactionList = message.getReactions();
        var upvoteEmoji = EmoteUtil.getEmoji("upvote");
        MessageReaction signupReaction = null;
        for (var reaction : reactionList)
            if (reaction.getReactionEmote().getName().equals(upvoteEmoji))
                signupReaction = reaction;

        if (signupReaction == null) return;

        signupReaction.retrieveUsers().queue(users -> {
            for (int i = 0; i < users.size(); i++) {
                var user = users.get(i);
                if (user.isBot()) {
                    users.remove(user);
                    i--;
                }
            }
            if (users.size() == amount || forceEnd) {
                String lang = server.getLanguage();
                var eb = new EmbedBuilder();
                var signupUser = message.getJDA().getUserById(signup.getAuthorId());
                if (signupUser == null) return;
                eb.setColor(new Master(signupUser).getColor());
                var title = signup.getTitle();
                eb.setTitle(title.isEmpty() ? LanguageHandler.get(lang, "signup_embedtitle_empty") :
                        String.format(LanguageHandler.get(lang, "signup_embedtitle_notempty"), title));
                eb.setDescription(String.format(LanguageHandler.get(lang, "signup_embeddescriptionend"), amount) + "\n");
                if (users.isEmpty()) eb.appendDescription(LanguageHandler.get(lang, "signup_nobody"));
                else for (var user : users) eb.appendDescription(user.getAsMention() + "\n");
                eb.setFooter(signup.isCustomDate() ?
                                LanguageHandler.get(lang, "signup_event") :
                                LanguageHandler.get(lang, "signup_timeout_finish"),
                        ImageUtil.getImageUrl(jda, "clock"));
                eb.setTimestamp(expiration);
                message.editMessage(eb.build()).queue();
                message.clearReactions().queue();
                signup.unset();
            }
        });
    }
}
