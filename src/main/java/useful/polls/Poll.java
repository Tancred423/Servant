package useful.polls;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import servant.Log;
import servant.Servant;
import utilities.Emote;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static utilities.DatabaseConn.closeQuietly;

public class Poll {
    public static void check(JDA jda) {
        Guild guild = null;
        User user = jda.getSelfUser();

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

                    if (nowMillis >= endingDateMillis) {
                        guild = jda.getGuildById(guildId);
                        if (guild == null) {
                            removePoll(messageId, null, user);
                        } else {
                            var finalGuild = guild;
                            var textChannel = guild.getTextChannelById(channelId);
                            if (textChannel == null) {
                                removePoll(messageId, finalGuild, user);
                            } else {
                                var lang = new moderation.guild.Guild(guildId).getLanguage(finalGuild, user);
                                switch (type) {
                                    case "quick":
                                        textChannel.retrieveMessageById(messageId).queue(message ->
                                                        endQuickpoll(finalGuild, user, message, lang, jda),
                                                failure -> removePoll(messageId, finalGuild, user));
                                        break;

                                    case "radio":
                                    case "vote":
                                        textChannel.retrieveMessageById(messageId).queue(message ->
                                                        endPoll(finalGuild, user, message, lang, jda),
                                                failure -> removePoll(messageId, finalGuild, user));
                                        break;

                                    default:
                                        removePoll(messageId, guild, user);
                                        break;
                                }
                            }
                        }
                    }
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "poll", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    private static void removePoll(long messageId, Guild guild, User user) {
        PollsDatabase.unsetVote(messageId, guild, user);
        PollsDatabase.unsetUserVotes(messageId, guild, user);
    }

    public static void endPoll(net.dv8tion.jda.api.entities.Guild guild, User user, Message message, String lang, JDA jda) {
        Map<Integer, Integer> count = new HashMap<>();
        for (int i = 0; i < 10; i++) count.put(i + 1, 0);

        net.dv8tion.jda.api.entities.Emote oneEmote;
        net.dv8tion.jda.api.entities.Emote twoEmote;
        net.dv8tion.jda.api.entities.Emote threeEmote;
        net.dv8tion.jda.api.entities.Emote fourEmote;
        net.dv8tion.jda.api.entities.Emote fiveEmote;
        net.dv8tion.jda.api.entities.Emote sixEmote;
        net.dv8tion.jda.api.entities.Emote sevenEmote;
        net.dv8tion.jda.api.entities.Emote eightEmote;
        net.dv8tion.jda.api.entities.Emote nineEmote;
        net.dv8tion.jda.api.entities.Emote tenEmote;

        oneEmote = Emote.getEmote("one", guild, user);
        twoEmote = Emote.getEmote("two", guild, user);
        threeEmote = Emote.getEmote("three", guild, user);
        fourEmote = Emote.getEmote("four", guild, user);
        fiveEmote = Emote.getEmote("five", guild, user);
        sixEmote = Emote.getEmote("six", guild, user);
        sevenEmote = Emote.getEmote("seven", guild, user);
        eightEmote = Emote.getEmote("eight", guild, user);
        nineEmote = Emote.getEmote("nine", guild, user);
        tenEmote = Emote.getEmote("ten", guild, user);

        var oneEmoji = Emote.getEmoji("one");
        var twoEmoji = Emote.getEmoji("two");
        var threeEmoji = Emote.getEmoji("three");
        var fourEmoji = Emote.getEmoji("four");
        var fiveEmoji = Emote.getEmoji("five");
        var sixEmoji = Emote.getEmoji("six");
        var sevenEmoji = Emote.getEmoji("seven");
        var eightEmoji = Emote.getEmoji("eight");
        var nineEmoji = Emote.getEmoji("nine");
        var tenEmoji = Emote.getEmoji("ten");

        var reactions = message.getReactions();
        for (var reaction : reactions) {
            var emote = reaction.getReactionEmote();

            if (emote.isEmote()) {
                if (emote.getEmote().equals(oneEmote)) count.put(1, reaction.getCount() - 1);
                if (emote.getEmote().equals(twoEmote)) count.put(2, reaction.getCount() - 1);
                if (emote.getEmote().equals(threeEmote)) count.put(3, reaction.getCount() - 1);
                if (emote.getEmote().equals(fourEmote)) count.put(4, reaction.getCount() - 1);
                if (emote.getEmote().equals(fiveEmote)) count.put(5, reaction.getCount() - 1);
                if (emote.getEmote().equals(sixEmote)) count.put(6, reaction.getCount() - 1);
                if (emote.getEmote().equals(sevenEmote)) count.put(7, reaction.getCount() - 1);
                if (emote.getEmote().equals(eightEmote)) count.put(8, reaction.getCount() - 1);
                if (emote.getEmote().equals(nineEmote)) count.put(9, reaction.getCount() - 1);
                if (emote.getEmote().equals(tenEmote)) count.put(10, reaction.getCount() - 1);
            } else {
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
        eb.setAuthor(String.format(LanguageHandler.get(lang, "vote_ended"), user.getName()), null, author.getIconUrl());
        eb.setTitle(field.getName());
        for (int i = 0; i < lines.size(); i++) eb.addField(lines.get(i), String.valueOf(count.get(i + 1)), true);
        eb.setFooter(LanguageHandler.get(lang, "votes_inactive"), jda.getSelfUser().getAvatarUrl());

        message.editMessage(eb.build()).queue();
        message.clearReactions().queue();
        removePoll(message.getIdLong(), guild, user);
    }

    public static void endQuickpoll(net.dv8tion.jda.api.entities.Guild guild, User user, Message message, String lang, JDA jda) {
        var upvoteCount = 0;
        var downvoteCount = 0;

        var upvoteEmoji = Emote.getEmoji("upvote");
        var downvoteEmoji = Emote.getEmoji("downvote");

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
        eb.setAuthor(String.format(LanguageHandler.get(lang, "quickvote_ended"), user.getName()), null, author.getIconUrl());
        eb.setDescription(messageEmbed.getDescription());
        eb.addField(upvoteEmoji, String.valueOf(upvoteCount), true);
        eb.addField(downvoteEmoji, String.valueOf(downvoteCount), true);
        eb.setFooter(LanguageHandler.get(lang, "votes_inactive"), jda.getSelfUser().getAvatarUrl());

        message.editMessage(eb.build()).queue();
        message.clearReactions().queue();
        removePoll(message.getIdLong(), guild, user);
    }
}
