package commands.utility.rate;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import servant.*;
import utilities.Console;
import utilities.EmoteUtil;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static servant.Database.closeQuietly;

public class Rating {
    private final JDA jda;
    private final String lang;
    private final long guildId;
    private final long tcId;
    private final long msgId;

    public Rating(JDA jda, String lang, long guildId, long tcId, long msgId) {
        this.jda = jda;
        this.lang = lang;
        this.guildId = guildId;
        this.tcId = tcId;
        this.msgId = msgId;
    }

    // Get
    public long getGuildId() {
        return guildId;
    }

    public Guild getGuild() {
        return jda.getGuildById(getGuildId());
    }

    public long getTcId() {
        return tcId;
    }

    public TextChannel getTc() {
        var guild = getGuild();
        if (guild == null) return null;
        else return guild.getTextChannelById(getTcId());
    }

    public long getAuthorId() {
        Connection connection = null;
        var authorId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT author_id " +
                            "FROM ratings " +
                            "WHERE msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) authorId = resultSet.getLong("author_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Rating#getAuthorId"));
        } finally {
            closeQuietly(connection);
        }

        return authorId;
    }

    public User getAuthor() {
        return jda.getUserById(getAuthorId());
    }

    public Member getAuthorMember() {
        var guild = getGuild();
        if (guild == null) return null;
        else return guild.getMemberById(getAuthorId());
    }

    public Instant getEventTime() {
        Connection connection = null;
        Instant eventTime = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT event_time " +
                            "FROM ratings " +
                            "WHERE msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) eventTime = resultSet.getTimestamp("event_time").toInstant();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Rating#getEndingDate"));
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
                            "FROM ratings " +
                            "WHERE msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) {
                topic = resultSet.getString("topic");
                if (topic.isEmpty()) topic = null;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Rating#getTopic"));
        } finally {
            closeQuietly(connection);
        }

        return topic;
    }

    public ArrayList<Vote> getParticipants() {
        Connection connection = null;
        var participants = new ArrayList<Vote>();

        if (new MyMessage(jda, guildId, tcId, msgId).isRating()) {
            try {
                connection = Servant.db.getHikari().getConnection();
                var preparedStatement = connection.prepareStatement(
                        "SELECT user_id, reaction " +
                                "FROM tmp_rating_participants " +
                                "WHERE msg_id=?");
                preparedStatement.setLong(1, msgId);
                var resultSet = preparedStatement.executeQuery();
                while (resultSet.next())
                    participants.add(new Vote(resultSet.getLong("user_id"), resultSet.getString("reaction")));
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Rating#getParticipants"));
            } finally {
                closeQuietly(connection);
            }
        }

        return participants;
    }

    // Set
    public void set(long authorId, Instant eventTime, String topic) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement(
                    "INSERT INTO ratings (guild_id,tc_id,msg_id,author_id,event_time,topic) " +
                            "VALUES (?,?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, tcId);
            insert.setLong(3, msgId);
            insert.setLong(4, authorId);
            insert.setTimestamp(5, Timestamp.from(eventTime));
            insert.setString(6, topic == null ? "" : topic);
            insert.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Rating#set"));
        } finally {
            closeQuietly(connection);
        }
    }

    // Participants
    public void setParticipant(long userId, String emoji) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement(
                    "INSERT INTO tmp_rating_participants (msg_id,user_id,reaction,guild_id,tc_id) " +
                            "VALUES (?,?,?,?,?)");
            insert.setLong(1, msgId);
            insert.setLong(2, userId);
            insert.setString(3, emoji);
            insert.setLong(4, guildId);
            insert.setLong(5, tcId);
            insert.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Rating#setParticipant"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetParticipant(long userId, String emoji) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement(
                    "DELETE FROM tmp_rating_participants " +
                            "WHERE msg_id=? " +
                            "AND user_id=? " +
                            "AND reaction=?");
            delete.setLong(1, msgId);
            delete.setLong(2, userId);
            delete.setString(3, emoji);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Rating#unsetParticipant"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean hasParticipated(long userId) {
        Connection connection = null;
        var hasVoted = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * FROM tmp_rating_participants " +
                            "WHERE msg_id=? " +
                            "AND user_id=?");
            select.setLong(1, msgId);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) hasVoted = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Rating#hasParticipated"));
        } finally {
            closeQuietly(connection);
        }

        return hasVoted;
    }

    public String getParticipantEmoji(long userId) {
        Connection connection = null;
        var emote = "";

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT reaction " +
                            "FROM tmp_rating_participants " +
                            "WHERE msg_id=? " +
                            "AND user_id=?");
            select.setLong(1, msgId);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            if (resultSet.next()) emote = resultSet.getString("reaction");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Rating#getParticipantEmoji"));
        } finally {
            closeQuietly(connection);
        }

        return emote;
    }

    // End
    public void end() {
        var oneEmoji = EmoteUtil.getEmoji(jda, "one");
        var twoEmoji = EmoteUtil.getEmoji(jda, "two");
        var threeEmoji = EmoteUtil.getEmoji(jda, "three");
        var fourEmoji = EmoteUtil.getEmoji(jda, "four");
        var fiveEmoji = EmoteUtil.getEmoji(jda, "five");

        var counts = new LinkedHashMap<String, Integer>();
        counts.put(oneEmoji, 0);
        counts.put(twoEmoji, 0);
        counts.put(threeEmoji, 0);
        counts.put(fourEmoji, 0);
        counts.put(fiveEmoji, 0);


        var participants = getParticipants();
        for (var participant : participants) {
            if (participant.getReaction().equals(oneEmoji)) counts.put(oneEmoji, counts.get(oneEmoji) + 1);
            else if (participant.getReaction().equals(twoEmoji)) counts.put(twoEmoji, counts.get(twoEmoji) + 1);
            else if (participant.getReaction().equals(threeEmoji)) counts.put(threeEmoji, counts.get(threeEmoji) + 1);
            else if (participant.getReaction().equals(fourEmoji)) counts.put(fourEmoji, counts.get(fourEmoji) + 1);
            else if (participant.getReaction().equals(fiveEmoji)) counts.put(fiveEmoji, counts.get(fiveEmoji) + 1);
        }

        var tc = getTc();
        if (tc == null) {
            purge();
            return;
        }

        tc.retrieveMessageById(msgId).queue(message -> {
            var eb = new EmbedBuilder();

            var author = getAuthor();
            if (author == null) {
                purge();
                return;
            }

            var topic = getTopic();
            var oneVotes = (float) counts.get(oneEmoji);
            var twoVotes = (float) counts.get(twoEmoji);
            var threeVotes = (float) counts.get(threeEmoji);
            var fourVotes = (float) counts.get(fourEmoji);
            var fiveVotes = (float) counts.get(fiveEmoji);

            var average = oneVotes + twoVotes + threeVotes + fourVotes + fiveVotes == 0 ? 0 :
                    (oneVotes + 2 * twoVotes + 3 * threeVotes + 4 * fourVotes + 5 * fiveVotes) / (oneVotes + twoVotes + threeVotes + fourVotes + fiveVotes);

            eb.setColor(Color.decode(new MyUser(author).getColorCode()));
            eb.setAuthor(topic == null ? LanguageHandler.get(lang, "rate_title") : String.format(LanguageHandler.get(lang, "rate_title_topic"), topic), null, author.getEffectiveAvatarUrl());
            eb.setDescription("Ø⭐ " + average);
            for (var count : counts.entrySet()) eb.addField(count.getKey(), String.valueOf(count.getValue()), true);
            eb.setFooter(LanguageHandler.get(lang, "rate_ended"), null);

            var guild = message.getGuild();
            var selfMember = guild.getMemberById(jda.getSelfUser().getIdLong());
            if (selfMember != null && selfMember.hasPermission(Permission.MESSAGE_WRITE) && message.getTextChannel().canTalk(selfMember)) {
                message.editMessage(eb.build()).queue();
                message.clearReactions().queue();
                purge();
            } else {
                Console.log("Missing permissions MESSAGE_WRITE (Rating). Guild: " + guild.getName() + " (" + guild.getIdLong() + ") | TC: " + tc.getName() + " (" + tc.getIdLong() + ")");
//                new MyGuild(guild).purgeMsg(tcId, msgId);
            }
        }, f -> new MyGuild(tc.getGuild()).purgeMsg(tc.getIdLong(), msgId));
    }

    // Purge
    public void purge() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement(
                    "DELETE FROM ratings " +
                            "WHERE msg_id=?");
            delete.setLong(1, msgId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Rating#purge"));
        } finally {
            closeQuietly(connection);
        }

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement(
                    "DELETE FROM tmp_rating_participants " +
                            "WHERE msg_id=?");
            delete.setLong(1, msgId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Rating#purge"));
        } finally {
            closeQuietly(connection);
        }
    }

    // Static
    public static List<Rating> getList(JDA jda) {
        Connection connection = null;
        var ratings = new ArrayList<Rating>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM ratings");
            var resultSet = select.executeQuery();
            while (resultSet.next()) {
                var guildId = resultSet.getLong("guild_id");
                var guild = jda.getGuildById(guildId);
                if (guild == null) continue;
                var lang = new MyGuild(guild).getLanguageCode();
                ratings.add(new Rating(jda, lang, guildId, resultSet.getLong("tc_id"), resultSet.getLong("msg_id")));
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Rating#getList"));
        } finally {
            closeQuietly(connection);
        }

        return ratings;
    }
}
