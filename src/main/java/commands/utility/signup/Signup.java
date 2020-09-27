package commands.utility.signup;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import servant.*;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static servant.Database.closeQuietly;

public class Signup {
    private final JDA jda;
    private final long guildId;
    private final long tcId;
    private final long msgId;

    public Signup(JDA jda, long guildId, long tcId, long msgId) {
        this.jda = jda;
        this.guildId = guildId;
        this.tcId = tcId;
        this.msgId = msgId;
    }

    public long getAuthorId() {
        Connection connection = null;
        var authorId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT author_id " +
                            "FROM signups " +
                            "WHERE msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) authorId = resultSet.getLong("author_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Signup#getAuthorId"));
        } finally {
            closeQuietly(connection);
        }

        return authorId;
    }

    public int getAmountParticipants() {
        Connection connection = null;
        var amount = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT amount_participants " +
                            "FROM signups " +
                            "WHERE msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) amount = resultSet.getInt("amount_participants");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Signup#getAmountParticipants"));
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
            var select = connection.prepareStatement(
                    "SELECT title " +
                            "FROM signups " +
                            "WHERE msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) authorId = resultSet.getString("title");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Signup#getTitle"));
        } finally {
            closeQuietly(connection);
        }

        return authorId;
    }

    public Timestamp getEventTime() {
        Connection connection = null;
        Timestamp expiration = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT event_time " +
                            "FROM signups " +
                            "WHERE msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) expiration = resultSet.getTimestamp("event_time");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Signup#getEventTime"));
        } finally {
            closeQuietly(connection);
        }

        return expiration;
    }

    public void set(long authorId, int amount, String title, Timestamp eventTime) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement(
                    "INSERT INTO signups (guild_id,tc_id,msg_id,author_id,amount_participants,title,event_time) " +
                            "VALUES (?,?,?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, tcId);
            insert.setLong(3, msgId);
            insert.setLong(4, authorId);
            insert.setInt(5, amount);
            insert.setString(6, title == null ? "" : title);
            insert.setTimestamp(7, eventTime);
            insert.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Signup#set"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void purge() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM signups WHERE msg_id=?");
            delete.setLong(1, msgId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Signup#purge"));
        } finally {
            closeQuietly(connection);
        }

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement(
                    "DELETE FROM tmp_signup_participants " +
                            "WHERE msg_id=?");
            delete.setLong(1, msgId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Signup#purge"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void end() {
        var myMessage = new MyMessage(jda, guildId, tcId, msgId);
        if (!myMessage.isSignup()) return;

        var authorId = getAuthorId();
        var author = jda.getUserById(authorId);
        if (author == null) {
            purge();
            return;
        }

        var guild = jda.getGuildById(guildId);
        if (guild == null) {
            purge();
            return;
        }

        var tc = guild.getTextChannelById(tcId);
        if (tc == null) {
            purge();
            return;
        }

        tc.retrieveMessageById(msgId).queue(message -> {
            if (message == null) {
                purge();
                return;
            }

            var participants = getParticipants();
            var title = getTitle();
            var myGuild = new MyGuild(guild);
            var lang = myGuild.getLanguageCode();
            var myAuthor = new MyUser(author);

            var eb = new EmbedBuilder();
            eb.setColor(Color.decode(myAuthor.getColorCode()));
            eb.setTitle(title.isEmpty() ? LanguageHandler.get(lang, "signup_embedtitle") :
                    String.format(LanguageHandler.get(lang, "signup_embedtitle_topic"), title));
            eb.setDescription(String.format(LanguageHandler.get(lang, "signup_embeddescriptionend"), getAmountParticipants()) + "\n");

            var sb = new StringBuilder();
            if (participants.isEmpty()) eb.addField("", LanguageHandler.get(lang, "signup_nobody"), false);
            else {
                for (var participant : participants) {
                    var member = guild.getMemberById(participant);
                    if (member == null) continue;
                    var user = member.getUser();

                    if (sb.toString().length() + user.getAsMention().length() + 2 > 1024) {
                        eb.addField("", sb.toString(), true);
                        sb = new StringBuilder();
                    }

                    sb.append(user.getAsMention()).append("\n");
                }
            }
            eb.addField("", sb.toString(), true);
            eb.setFooter(LanguageHandler.get(lang, "signup_ended"), null);

            message.editMessage(eb.build()).queue();
            message.clearReactions().queue();
            purge();
        });
    }

    public ArrayList<Long> getParticipants() {
        Connection connection = null;
        var participants = new ArrayList<Long>();

        if (new MyMessage(jda, guildId, tcId, msgId).isSignup()) {
            try {
                connection = Servant.db.getHikari().getConnection();
                var preparedStatement = connection.prepareStatement(
                        "SELECT user_id " +
                                "FROM tmp_signup_participants " +
                                "WHERE msg_id=?");
                preparedStatement.setLong(1, msgId);
                var resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) participants.add(resultSet.getLong("user_id"));
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Signup#getParticipants"));
            } finally {
                closeQuietly(connection);
            }
        }

        return participants;
    }

    public static List<Signup> getList(JDA jda) {
        Connection connection = null;
        var signups = new ArrayList<Signup>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * " +
                            "FROM signups");
            var resultSet = select.executeQuery();
            while (resultSet.next()) {
                signups.add(new Signup(
                        jda,
                        resultSet.getLong("guild_id"),
                        resultSet.getLong("tc_id"),
                        resultSet.getLong("msg_id")
                ));
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Signup#getList"));
        } finally {
            closeQuietly(connection);
        }

        return signups;
    }

    public boolean setParticipant(long userId) {
        var wasSet = false;
        if (new MyMessage(jda, guildId, tcId, msgId).isSignup() && !getParticipants().contains(userId)) {
            Connection connection = null;
            try {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement(
                        "INSERT INTO tmp_signup_participants (msg_id,user_id,guild_id,tc_id) " +
                                "VALUES (?,?,?,?)");
                insert.setLong(1, msgId);
                insert.setLong(2, userId);
                insert.setLong(3, guildId);
                insert.setLong(4, tcId);
                insert.executeUpdate();
                wasSet = true;
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Signup#setParticipant"));
            } finally {
                closeQuietly(connection);
            }
        }
        return wasSet;
    }

    public void unsetParticipant(long userId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement(
                    "DELETE FROM tmp_signup_participants " +
                            "WHERE msg_id=? " +
                            "AND user_id=?");
            delete.setLong(1, msgId);
            delete.setLong(2, userId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Signup#unsetParticipant"));
        } finally {
            closeQuietly(connection);
        }
    }
}
