// Author: Tancred423 (https://github.com/Tancred423)
package useful.polls;

import files.language.LanguageHandler;
import moderation.guild.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import servant.LoggingTask;
import servant.Servant;
import utilities.EmoteUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static servant.Database.closeQuietly;

public class Poll {
    private JDA jda;
    private String lang;
    private Message message;

    public Poll(JDA jda, String lang, Message message) {
        this.jda = jda;
        this.lang = lang;
        this.message = message;
    }

    public void set(long guildId, long channelId, long authorId, String type, Timestamp endingDate) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("INSERT INTO votes (guild_id,channel_id,message_id,author_id,type,ending_date) VALUES (?,?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, channelId);
            insert.setLong(3, message.getIdLong());
            insert.setLong(4, authorId);
            insert.setString(5, type);
            insert.setTimestamp(6, endingDate);
            insert.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#set"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void unset() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM votes WHERE message_id=?");
            delete.setLong(1, message.getIdLong());
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#unsetPoll"));
        } finally {
            closeQuietly(connection);
        }

        unsetVotes();
    }

    public boolean isQuickPoll() {
        Connection connection = null;
        var isQuickvote = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT type FROM votes WHERE message_id=?");
            select.setLong(1, message.getIdLong());
            var resultSet = select.executeQuery();
            if (resultSet.first()) if (resultSet.getString("type").equals("quick")) isQuickvote = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#isQuickPoll"));
        } finally {
            closeQuietly(connection);
        }

        return isQuickvote;
    }

    public boolean isPoll() {
        Connection connection = null;
        var isPoll = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT type FROM votes WHERE message_id=?");
            select.setLong(1, message.getIdLong());
            var resultSet = select.executeQuery();
            if (resultSet.first()) if (resultSet.getString("type").equals("vote")) isPoll = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#isPoll"));
        } finally {
            closeQuietly(connection);
        }

        return isPoll;
    }

    public boolean isRadioPoll() {
        Connection connection = null;
        var isRadiovote = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT type FROM votes WHERE message_id=?");
            select.setLong(1, message.getIdLong());
            var resultSet = select.executeQuery();
            if (resultSet.first()) if (resultSet.getString("type").equals("radio")) isRadiovote = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#isRadioPoll"));
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
            var select = connection.prepareStatement("SELECT author_id FROM votes WHERE message_id=?");
            select.setLong(1, message.getIdLong());
            var resultSet = select.executeQuery();
            if (resultSet.first()) authorId = resultSet.getLong("author_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#getAuthorId"));
        } finally {
            closeQuietly(connection);
        }

        return authorId;
    }

    public void setVote(long userId, long emoteId, String emoji) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("INSERT INTO user_votes (message_id,user_id,emote_id,emoji) VALUES (?,?,?,?)");
            insert.setLong(1, message.getIdLong());
            insert.setLong(2, userId);
            insert.setLong(3, emoteId);
            insert.setString(4, emoji);
            insert.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#setVote"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetVote(long userId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM user_votes WHERE message_id=? AND user_id=?");
            delete.setLong(1, message.getIdLong());
            delete.setLong(2, userId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#unsetVote"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetVotes() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM user_votes WHERE message_id=?");
            delete.setLong(1, message.getIdLong());
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#unsetVotes"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean hasVoted(long userId) {
        Connection connection = null;
        var hasVoted = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM user_votes WHERE message_id=? AND user_id=?");
            select.setLong(1, message.getIdLong());
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

    public long getVoteEmoteId(long userId) {
        Connection connection = null;
        var id = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT emote_id FROM user_votes WHERE message_id=? AND user_id=?");
            select.setLong(1, message.getIdLong());
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) id = resultSet.getLong("emote_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#getEmoteId"));
        } finally {
            closeQuietly(connection);
        }

        return id;
    }

    public String getVoteEmoji(long userId) {
        Connection connection = null;
        var emote = "";

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT emoji FROM user_votes WHERE message_id=? AND user_id=?");
            select.setLong(1, message.getIdLong());
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) emote = resultSet.getString("emoji");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Poll#getEmoji"));
        } finally {
            closeQuietly(connection);
        }

        return emote;
    }

    public void end() {
        if (isQuickPoll()) endQuickPoll();
        else endPoll();
    }

    private void endPoll() {
        Map<Integer, Integer> count = new HashMap<>();
        for (int i = 0; i < 10; i++) count.put(i + 1, 0);

        var oneEmoji = EmoteUtil.getEmoji("one");
        var twoEmoji = EmoteUtil.getEmoji("two");
        var threeEmoji = EmoteUtil.getEmoji("three");
        var fourEmoji = EmoteUtil.getEmoji("four");
        var fiveEmoji = EmoteUtil.getEmoji("five");
        var sixEmoji = EmoteUtil.getEmoji("six");
        var sevenEmoji = EmoteUtil.getEmoji("seven");
        var eightEmoji = EmoteUtil.getEmoji("eight");
        var nineEmoji = EmoteUtil.getEmoji("nine");
        var tenEmoji = EmoteUtil.getEmoji("ten");

        var reactions = message.getReactions();
        for (var reaction : reactions) {
            var emote = reaction.getReactionEmote();

            if (!emote.isEmote()) {
                if (emote.getName().equals(oneEmoji)) count.put(1, reaction.getCount() - 1);
                if (emote.getName().equals(twoEmoji)) count.put(2, reaction.getCount() - 1);
                if (emote.getName().equals(threeEmoji)) count.put(3, reaction.getCount() - 1);
                if (emote.getName().equals(fourEmoji)) count.put(4, reaction.getCount() - 1);
                if (emote.getName().equals(fiveEmoji)) count.put(5, reaction.getCount() - 1);
                if (emote.getName().equals(sixEmoji)) count.put(6, reaction.getCount() - 1);
                if (emote.getName().equals(sevenEmoji)) count.put(7, reaction.getCount() - 1);
                if (emote.getName().equals(eightEmoji)) count.put(8, reaction.getCount() - 1);
                if (emote.getName().equals(nineEmoji)) count.put(9, reaction.getCount() - 1);
                if (emote.getName().equals(tenEmoji)) count.put(10, reaction.getCount() - 1);
            }
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
        eb.setAuthor(String.format(LanguageHandler.get(lang, "vote_ended"), author.getName()), null, author.getIconUrl());
        eb.setTitle(field.getName());
        for (int i = 0; i < lines.size(); i++) eb.addField(shortenTitle(lines.get(i)), String.valueOf(count.get(i + 1)), true);
        eb.setFooter(LanguageHandler.get(lang, "votes_inactive"), jda.getSelfUser().getAvatarUrl());

        message.editMessage(eb.build()).queue();
        message.clearReactions().queue();
        unset();
    }

    private String shortenTitle(String title) {
        if (title.length() > 256) {
            title = title.substring(0, 256 - 3);
            title += "...";
        }

        return title;
    }

    public void endQuickPoll() {
        var upvoteCount = 0;
        var downvoteCount = 0;

        var upvoteEmoji = EmoteUtil.getEmoji("upvote");
        var downvoteEmoji = EmoteUtil.getEmoji("downvote");

        var reactions = message.getReactions();
        for (var reaction : reactions) {
            var emote = reaction.getReactionEmote();
            if (emote.getName().equals(upvoteEmoji)) upvoteCount = reaction.getCount() - 1;
            if (emote.getName().equals(downvoteEmoji)) downvoteCount = reaction.getCount() - 1;
        }

        var messageEmbed = message.getEmbeds().get(0);
        var author = messageEmbed.getAuthor();
        if (author == null) return;

        var eb = new EmbedBuilder();
        eb.setColor(messageEmbed.getColor());
        eb.setAuthor(String.format(LanguageHandler.get(lang, "quickvote_ended"), author.getName()), null, author.getIconUrl());
        eb.setDescription(messageEmbed.getDescription());
        eb.addField(upvoteEmoji, String.valueOf(upvoteCount), true);
        eb.addField(downvoteEmoji, String.valueOf(downvoteCount), true);
        eb.setFooter(LanguageHandler.get(lang, "votes_inactive"), jda.getSelfUser().getAvatarUrl());

        message.editMessage(eb.build()).queue();
        message.clearReactions().queue();
        unset();
    }

    public static void check(JDA jda) {
        Connection connection = null;
        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM votes");
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do {
                    var guildId = resultSet.getLong("guild_id");
                    var channelId = resultSet.getLong("channel_id");
                    var messageId = resultSet.getLong("message_id");
                    var type = resultSet.getString("type");
                    var endingDate = resultSet.getTimestamp("ending_date");
                    var endingDateMillis = endingDate.getTime();
                    var nowMillis = OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();

                    var guild = jda.getGuildById(guildId);
                    if (guild == null) return;

                    var channel = guild.getTextChannelById(channelId);
                    if (channel == null) return;

                    channel.retrieveMessageById(messageId).queue(message -> {
                        var guild1 = jda.getGuildById(guildId);
                        if (guild1 == null) return;

                        var lang = new Server(guild1).getLanguage();
                        var poll = new Poll(jda, lang, message);

                        if (nowMillis >= endingDateMillis) {
                            guild1 = jda.getGuildById(guildId);
                            if (guild1 == null) {
                                poll.unset();
                            } else {
                                var textChannel = guild1.getTextChannelById(channelId);
                                if (textChannel == null) {
                                    poll.unset();
                                } else {
                                    switch (type) {
                                        case "quick":
                                        case "radio":
                                        case "vote":
                                           poll.end();
                                            break;

                                        default:
                                            poll.unset();
                                            break;
                                    }
                                }
                            }
                        }
                    }, f -> {});
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "PollUtil#check"));
        } finally {
            closeQuietly(connection);
        }
    }
}
