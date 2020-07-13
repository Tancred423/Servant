package commands.utility.remindme;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import servant.*;
import utilities.ImageUtil;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static servant.Database.closeQuietly;

public class RemindMe {
    private final JDA jda;
    private final long guildId;
    private final long tcId;
    private final long msgId;

    public RemindMe(JDA jda, long guildId, long tcId, long msgId) {
        this.jda = jda;
        this.guildId = guildId;
        this.tcId = tcId;
        this.msgId = msgId;
    }

    public long getGuildId() { return guildId; }
    public long getTcId() { return tcId; }
    public long getMsgId() { return msgId; }

    public long getAuthorId() {
        Connection connection = null;
        var authorId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT author_id " +
                            "FROM remind_mes " +
                            "WHERE msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) authorId = resultSet.getLong("author_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "RemindMe#getAuthorId"));
        } finally {
            closeQuietly(connection);
        }

        return authorId;
    }

    public Timestamp getEventTime() {
        Connection connection = null;
        Timestamp eventTime = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT event_time " +
                            "FROM remind_mes " +
                            "WHERE msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) eventTime = resultSet.getTimestamp("event_time");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "RemindMe#getEventTime"));
        } finally {
            closeQuietly(connection);
        }

        return eventTime;
    }

    public String getTopic() {
        Connection connection = null;
        String topic = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT topic " +
                            "FROM remind_mes " +
                            "WHERE msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) topic = resultSet.getString("topic");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "RemindMe#getTopic"));
        } finally {
            closeQuietly(connection);
        }

        return topic;
    }

    public static List<RemindMe> getList(JDA jda) {
        Connection connection = null;
        var remindMes = new ArrayList<RemindMe>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * " +
                            "FROM remind_mes",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do {
                    remindMes.add(new RemindMe(
                            jda,
                            resultSet.getLong("guild_id"),
                            resultSet.getLong("tc_id"),
                            resultSet.getLong("msg_id")
                    ));
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "RemindMe#getList"));
        } finally {
            closeQuietly(connection);
        }

        return remindMes;
    }

    public boolean setParticipant(long userId) {
        var wasSet = false;
        if (new MyMessage(jda, guildId, tcId, msgId).isRemindMe() && !getParticipants().contains(userId)) {
            Connection connection = null;
            try {
                connection = Servant.db.getHikari().getConnection();
                var preparedStatement = connection.prepareStatement(
                        "INSERT INTO tmp_remindme_participants (msg_id,user_id) " +
                                "VALUES (?,?)");
                preparedStatement.setLong(1, msgId);
                preparedStatement.setLong(2, userId);
                preparedStatement.executeUpdate();
                wasSet = true;
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "RemindMe#setParticipant"));
            } finally {
                closeQuietly(connection);
            }
        }
        return wasSet;
    }

    public ArrayList<Long> getParticipants() {
        Connection connection = null;
        var participants = new ArrayList<Long>();

        if (new MyMessage(jda, guildId, tcId, msgId).isRemindMe()) {
            try {
                connection = Servant.db.getHikari().getConnection();
                var preparedStatement = connection.prepareStatement(
                        "SELECT user_id " +
                                "FROM tmp_remindme_participants " +
                                "WHERE msg_id=?");
                preparedStatement.setLong(1, msgId);
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.first()) {
                    do {
                        participants.add(resultSet.getLong("user_id"));
                    } while (resultSet.next());
                }
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "RemindMe#getParticipants"));
            } finally {
                closeQuietly(connection);
            }
        }

        return participants;
    }

    public void set(long authorId, Timestamp eventTime, String topic) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement(
                    "INSERT INTO remind_mes (guild_id,tc_id,msg_id,author_id,event_time,topic) " +
                            "VALUES (?,?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, tcId);
            insert.setLong(3, msgId);
            insert.setLong(4, authorId);
            insert.setTimestamp(5, eventTime);
            insert.setString(6, topic);
            insert.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "RemindMe#set"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void unset() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement(
                    "DELETE FROM remind_mes " +
                            "WHERE msg_id=?");
            insert.setLong(1, msgId);
            insert.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "RemindMe#unset"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void end() {
        var myMessage = new MyMessage(jda, guildId, tcId, msgId);
        if (!myMessage.isRemindMe()) return;

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
            participants.add(authorId);
            var topic = getTopic();


            var myAuthor = new MyUser(author);
            var myGuild = new MyGuild(guild);
            var lang = myGuild.getLanguageCode();

            message.editMessage(new EmbedBuilder()
                    .setColor(Color.decode(myAuthor.getColorCode()))
                    .setAuthor(String.format(LanguageHandler.get(lang, "remindme_of"), author.getName()), null, author.getEffectiveAvatarUrl())
                    .setThumbnail(ImageUtil.getUrl(jda, "pinged"))
                    .setDescription((topic.isEmpty() ? "" : String.format(LanguageHandler.get(lang, "remindme_topic"), topic)) + "\n" +
                            String.format(LanguageHandler.get(lang, "remindme_success"), ""))
                    .build()
            ).queue();


            for (var participant : participants) {
                var member = guild.getMemberById(participant);
                if (member == null) continue;
                var user = member.getUser();
                user.openPrivateChannel().queue(
                        pc -> pc.sendMessage(new EmbedBuilder()
                                .setColor(Color.decode(myAuthor.getColorCode()))
                                .setAuthor(LanguageHandler.get(lang, "remindme_remind"), null, null)
                                .setThumbnail(ImageUtil.getUrl(jda, "pinged"))
                                .setDescription((topic.isEmpty() ? "" : String.format(LanguageHandler.get(lang, "remindme_topic"), topic) + "\n") +
                                        "[" + LanguageHandler.get(lang, "remindme_jump") + "](" + message.getJumpUrl() + ")")
                                .build()).queue(),
                        failure -> {
                        });
            }

            message.clearReactions().queue();
            purge();
        });
    }

    public void purge() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();

            // RemindMes
            var delete = connection.prepareStatement(
                    "DELETE FROM remind_mes " +
                            "WHERE msg_id=?");
            delete.setLong(1, msgId);
            delete.executeUpdate();

            // RemindMe entries
            delete = connection.prepareStatement(
                    "DELETE FROM tmp_remindme_participants " +
                            "WHERE msg_id=?");
            delete.setLong(1, msgId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "RemindMe#purge"));
        } finally {
            closeQuietly(connection);
        }
    }
}
