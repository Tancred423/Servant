// Author: Tancred423 (https://github.com/Tancred423)
package commands.utility.polls;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import servant.LoggingTask;
import servant.MyGuild;
import servant.MyMessage;
import servant.Servant;
import utilities.EmoteUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static servant.Database.closeQuietly;

public class Poll {
    private final JDA jda;
    private final String lang;
    private final long guildId;
    private final long tcId;
    private final long msgId;

    public Poll(JDA jda, String lang, long guildId, long tcId, long msgId) {
        this.jda = jda;
        this.lang = lang;
        this.guildId = guildId;
        this.tcId = tcId;
        this.msgId = msgId;
    }

    public Instant getEventTime() {
        Connection connection = null;
        Instant eventTime = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT ending_date " +
                            "FROM polls " +
                            "WHERE msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) eventTime = resultSet.getTimestamp("ending_date").toInstant();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#getPollTypeIdByType"));
        } finally {
            closeQuietly(connection);
        }

        return eventTime;
    }

    public int getAmountAnswers() {
        Connection connection = null;
        var amountAnswers = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT amount_answers " +
                            "FROM polls " +
                            "WHERE msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                amountAnswers = resultSet.getInt("amount_answers");
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#getPollTypeIdByType"));
        } finally {
            closeQuietly(connection);
        }

        return amountAnswers;
    }

    private int getPollTypeIdByType(String type) {
        Connection connection = null;
        var pollTypeId = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT id " +
                            "FROM const_poll_types " +
                            "WHERE poll_type=?");
            select.setString(1, type);
            var resultSet = select.executeQuery();
            if (resultSet.first()) pollTypeId = resultSet.getInt("id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#getPollTypeIdByType"));
        } finally {
            closeQuietly(connection);
        }

        return pollTypeId;
    }

    public void set(long authorId, String type, Timestamp endingDate, int amountAnswers) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement(
                    "INSERT INTO polls (guild_id,tc_id,msg_id,author_id,poll_type_id,ending_date,amount_answers) " +
                            "VALUES (?,?,?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, tcId);
            insert.setLong(3, msgId);
            insert.setLong(4, authorId);
            insert.setInt(5, getPollTypeIdByType(type));
            insert.setTimestamp(6, endingDate);
            insert.setInt(7, amountAnswers);
            insert.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#set"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void purge() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement(
                    "DELETE FROM polls " +
                            "WHERE msg_id=?");
            delete.setLong(1, msgId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#purge"));
        } finally {
            closeQuietly(connection);
        }

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement(
                    "DELETE FROM tmp_poll_participants " +
                            "WHERE msg_id=?");
            delete.setLong(1, msgId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#purge"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean isCheck() {
        Connection connection = null;
        var isPoll = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT c.poll_type " +
                            "FROM polls AS p " +
                            "INNER JOIN const_poll_types AS c " +
                            "ON p.poll_type_id = c.id " +
                            "WHERE p.msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) if (resultSet.getString("poll_type").equals("check")) isPoll = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#isCheck"));
        } finally {
            closeQuietly(connection);
        }

        return isPoll;
    }

    public boolean isRadio() {
        Connection connection = null;
        var isRadiovote = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT c.poll_type " +
                            "FROM polls AS p " +
                            "INNER JOIN const_poll_types AS c " +
                            "ON p.poll_type_id = c.id " +
                            "WHERE p.msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) if (resultSet.getString("poll_type").equals("radio")) isRadiovote = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#isRadio"));
        } finally {
            closeQuietly(connection);
        }

        return isRadiovote;
    }

    public long getAuthorId() {
        Connection connection = null;
        var authorId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT author_id " +
                            "FROM polls " +
                            "WHERE msg_id=?");
            select.setLong(1, msgId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) authorId = resultSet.getLong("author_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#getAuthorId"));
        } finally {
            closeQuietly(connection);
        }

        return authorId;
    }

    // Votes
    public void setVote(long userId, String emoji) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement(
                    "INSERT INTO tmp_poll_participants (msg_id,user_id,reaction,guild_id,tc_id) " +
                            "VALUES (?,?,?,?,?)");
            insert.setLong(1, msgId);
            insert.setLong(2, userId);
            insert.setString(3, emoji);
            insert.setLong(4, guildId);
            insert.setLong(5, tcId);
            insert.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#setVote"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetVote(long userId, String emoji) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement(
                    "DELETE FROM tmp_poll_participants " +
                            "WHERE msg_id=? " +
                            "AND user_id=? " +
                            "AND reaction=?");
            delete.setLong(1, msgId);
            delete.setLong(2, userId);
            delete.setString(3, emoji);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#unsetVote"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean hasVoted(long userId) {
        Connection connection = null;
        var hasVoted = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * FROM tmp_poll_participants " +
                            "WHERE msg_id=? " +
                            "AND user_id=?");
            select.setLong(1, msgId);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) hasVoted = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#hasVoted"));
        } finally {
            closeQuietly(connection);
        }

        return hasVoted;
    }

    public String getVoteEmoji(long userId) {
        Connection connection = null;
        var emote = "";

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT reaction " +
                            "FROM tmp_poll_participants " +
                            "WHERE msg_id=? " +
                            "AND user_id=?");
            select.setLong(1, msgId);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) emote = resultSet.getString("reaction");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#getEmoji"));
        } finally {
            closeQuietly(connection);
        }

        return emote;
    }

    public void end() {
        if (new MyMessage(jda, guildId, tcId, msgId).isQuickpoll()) endQuickPoll();
        else endPoll();
    }

    public void endPoll() {
        Map<Integer, Integer> count = new HashMap<>();
        for (int i = 0; i < 10; i++) count.put(i + 1, 0);

        var oneEmoji = EmoteUtil.getEmoji(jda, "one");
        var twoEmoji = EmoteUtil.getEmoji(jda, "two");
        var threeEmoji = EmoteUtil.getEmoji(jda, "three");
        var fourEmoji = EmoteUtil.getEmoji(jda, "four");
        var fiveEmoji = EmoteUtil.getEmoji(jda, "five");
        var sixEmoji = EmoteUtil.getEmoji(jda, "six");
        var sevenEmoji = EmoteUtil.getEmoji(jda, "seven");
        var eightEmoji = EmoteUtil.getEmoji(jda, "eight");
        var nineEmoji = EmoteUtil.getEmoji(jda, "nine");
        var tenEmoji = EmoteUtil.getEmoji(jda, "ten");


        var participants = getParticipants();
        for (var participant : participants) {
            if (participant.getReaction().equals(oneEmoji)) count.put(1, count.get(1) + 1);
            else if (participant.getReaction().equals(twoEmoji)) count.put(2, count.get(2) + 1);
            else if (participant.getReaction().equals(threeEmoji)) count.put(3, count.get(3) + 1);
            else if (participant.getReaction().equals(fourEmoji)) count.put(4, count.get(4) + 1);
            else if (participant.getReaction().equals(fiveEmoji)) count.put(5, count.get(5) + 1);
            else if (participant.getReaction().equals(sixEmoji)) count.put(6, count.get(6) + 1);
            else if (participant.getReaction().equals(sevenEmoji)) count.put(7, count.get(7) + 1);
            else if (participant.getReaction().equals(eightEmoji)) count.put(8, count.get(8) + 1);
            else if (participant.getReaction().equals(nineEmoji)) count.put(9, count.get(9) + 1);
            else if (participant.getReaction().equals(tenEmoji)) count.put(10, count.get(10) + 1);
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
            // Get Author Name
            var authorName = message.getAuthor().getName();
            var sm = jda.getShardManager();
            if (sm != null) {
                var authorUser = sm.getUserById(getAuthorId());
                if (authorUser != null) authorName = authorUser.getName();
            }

            var messageEmbed = message.getEmbeds().get(0);
            var eb = new EmbedBuilder();
            var field = messageEmbed.getFields().get(0);
            var description = field.getValue();
            List<String> lines = new ArrayList<>();
            if (description != null) lines = Arrays.asList(description.split("\\r?\\n"));

            var author = messageEmbed.getAuthor();
            if (author == null) return;

            eb.setColor(messageEmbed.getColor());
            eb.setAuthor(String.format(LanguageHandler.get(lang, "poll_ended_manually"), authorName, null, author.getIconUrl()));
            eb.setTitle(field.getName());
            for (int i = 0; i < lines.size(); i++) eb.addField(shortenTitle(lines.get(i)), String.valueOf(count.get(i + 1)), true);
            eb.setFooter(LanguageHandler.get(lang, "poll_ended"), null);

            message.editMessage(eb.build()).queue();
            message.clearReactions().queue();
            purge();
        });
    }

    private String shortenTitle(String title) {
        if (title.length() > 256) {
            title = title.substring(0, 256 - 3);
            title += "...";
        }

        return title;
    }

    public ArrayList<Vote> getParticipants() {
        Connection connection = null;
        var participants = new ArrayList<Vote>();

        if (new MyMessage(jda, guildId, tcId, msgId).isQuickpoll()
                || new MyMessage(jda, guildId, tcId, msgId).isCheckpoll()
                || new MyMessage(jda, guildId, tcId, msgId).isRadiopoll()) {
            try {
                connection = Servant.db.getHikari().getConnection();
                var preparedStatement = connection.prepareStatement(
                        "SELECT user_id, reaction " +
                                "FROM tmp_poll_participants " +
                                "WHERE msg_id=?");
                preparedStatement.setLong(1, msgId);
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.first()) {
                    do {
                        participants.add(new Vote(resultSet.getLong("user_id"), resultSet.getString("reaction")));
                    } while (resultSet.next());
                }
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#getParticipants"));
            } finally {
                closeQuietly(connection);
            }
        }

        return participants;
    }

    public void endQuickPoll() {
        var upvoteEmoji = EmoteUtil.getEmoji(jda, "upvote");
        var downvoteEmoji = EmoteUtil.getEmoji(jda, "downvote");

        var participants = getParticipants();
        var upvoteCount = 0;
        var downvoteCount = 0;

        for (var participant : participants) {
            if (participant.getReaction().equals(EmoteUtil.getEmoji(jda, "upvote"))) upvoteCount++;
            else if (participant.getReaction().equals(EmoteUtil.getEmoji(jda, "downvote"))) downvoteCount++;
        }

        var guild = jda.getGuildById(guildId);
        if (guild == null) {
            end();
            return;
        }

        var tc = guild.getTextChannelById(tcId);
        if (tc == null) {
            end();
            return;
        }

        var finalUpvoteCount = upvoteCount;
        var finalDownvoteCount = downvoteCount;
        tc.retrieveMessageById(msgId).queue(message -> {
            var messageEmbed = message.getEmbeds().get(0);
            var authorId = getAuthorId();
            var authorMember = guild.getMemberById(authorId);
            var authorName = message.getAuthor().getName();
            var authorIcon = message.getAuthor().getEffectiveAvatarUrl();
            if (authorMember != null){
                authorName = authorMember.getUser().getName();
                authorIcon = authorMember.getUser().getEffectiveAvatarUrl();
            }

            var eb = new EmbedBuilder();
            eb.setColor(messageEmbed.getColor());
            eb.setAuthor(String.format(LanguageHandler.get(lang, "quickpoll_ended"), authorName), null, authorIcon);
            eb.setDescription(messageEmbed.getDescription() == null ? "" : parseDesc(messageEmbed.getDescription()));
            eb.addField(upvoteEmoji, String.valueOf(finalUpvoteCount), true);
            eb.addField(downvoteEmoji, String.valueOf(finalDownvoteCount), true);
            eb.setFooter(LanguageHandler.get(lang, "poll_ended"), null);

            message.editMessage(eb.build()).queue();
            message.clearReactions().queue();
            purge();
        }, f -> {});
    }

    private String parseDesc(String desc) {
        return desc.split("<")[0];
    }

    public static void check(JDA jda) {
        Connection connection = null;
        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * " +
                            "FROM polls AS p " +
                            "INNER JOIN const_poll_types AS c " +
                            "ON p.poll_type_id = c.id");
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do {
                    var guildId = resultSet.getLong("guild_id");
                    var tcId = resultSet.getLong("tc_id");
                    var msgId = resultSet.getLong("msg_id");
                    var type = resultSet.getString("poll_type");
                    var endingDate = resultSet.getTimestamp("ending_date");
                    var endingDateMillis = endingDate.getTime();
                    var nowMillis = OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();

                    var guild1 = jda.getGuildById(guildId);
                    if (guild1 == null) return;

                    var lang = new MyGuild(guild1).getLanguageCode();
                    var poll = new Poll(jda, lang, guildId, tcId, msgId);

                    if (nowMillis >= endingDateMillis) {
                        guild1 = jda.getGuildById(guildId);
                        if (guild1 == null) {
                            poll.purge();
                        } else {
                            var textChannel = guild1.getTextChannelById(tcId);
                            if (textChannel == null) {
                                poll.purge();
                            } else {
                                switch (type) {
                                    case "quick":
                                    case "radio":
                                    case "check":
                                        poll.end();
                                        break;

                                    default:
                                        poll.purge();
                                        break;
                                }
                            }
                        }
                    }
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "PollUtil#check"));
        } finally {
            closeQuietly(connection);
        }
    }

    public static ArrayList<String> getPollEmojis(JDA jda) {
        var pollEmojis = new ArrayList<String>();
        pollEmojis.add(EmoteUtil.getEmoji(jda, "one"));
        pollEmojis.add(EmoteUtil.getEmoji(jda, "two"));
        pollEmojis.add(EmoteUtil.getEmoji(jda, "three"));
        pollEmojis.add(EmoteUtil.getEmoji(jda, "four"));
        pollEmojis.add(EmoteUtil.getEmoji(jda, "five"));
        pollEmojis.add(EmoteUtil.getEmoji(jda, "six"));
        pollEmojis.add(EmoteUtil.getEmoji(jda, "seven"));
        pollEmojis.add(EmoteUtil.getEmoji(jda, "eight"));
        pollEmojis.add(EmoteUtil.getEmoji(jda, "nine"));
        pollEmojis.add(EmoteUtil.getEmoji(jda, "ten"));

        return pollEmojis;
    }

    public static List<Poll> getList(JDA jda, String type) {
        Connection connection = null;
        var polls = new ArrayList<Poll>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT * " +
                            "FROM polls AS p " +
                            "INNER JOIN const_poll_types AS t " +
                            "ON p.poll_type_id=t.id " +
                            "WHERE poll_type=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            select.setString(1, type);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do {
                    var guildId = resultSet.getLong("guild_id");
                    var guild = jda.getGuildById(guildId);
                    if (guild == null) continue;
                    var lang = new MyGuild(guild).getLanguageCode();
                    polls.add(new Poll(jda, lang, guildId, resultSet.getLong("tc_id"), resultSet.getLong("msg_id")));
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#getList"));
        } finally {
            closeQuietly(connection);
        }

        return polls;
    }
}
