package useful.signup;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import servant.Log;
import servant.Servant;
import useful.giveaway.Giveaway;
import utilities.Emote;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static utilities.DatabaseConn.closeQuietly;

public class Signup {
    public static void checkSignups(JDA jda) {
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
                        var expiration = resultSet.getTimestamp("time").toLocalDateTime()
                                .atZone(ZoneId.of(new Guild(guildId).getOffset(guild, jda.getSelfUser())));
                        var author = jda.getUserById(resultSet.getLong("author_id"));
                        var now = ZonedDateTime.now(ZoneOffset.of(new Guild(guildId).getOffset(guild, author)));
                        var remainingTimeMillis = Giveaway.zonedDateTimeDifference(now, expiration);

                        if (remainingTimeMillis <= 0) {
                            var internalGuild = new Guild(guild.getIdLong());
                            var messageId = resultSet.getLong("message_id");
                            guild.getTextChannelById(resultSet.getLong("channel_id")).getMessageById(messageId).queue(message ->
                                    endSignup(internalGuild, messageId, message, guild, author, true));
                        }
                    }
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            new Log(e, null, jda.getSelfUser(), "signup", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    static void endSignup(Guild internalGuild, long messageId, Message message, net.dv8tion.jda.core.entities.Guild guild, net.dv8tion.jda.core.entities.User author, boolean forceEnd) {
        if (!internalGuild.isSignupMessage(messageId, guild, author)) return;
        var amount = internalGuild.getSignupAmount(messageId, guild, author);
        var reactionList = message.getReactions();
        var upvoteEmoji = Emote.getEmoji("upvote");
        MessageReaction signupReaction = null;
        for (var reaction : reactionList)
            if (reaction.getReactionEmote().getName().equals(upvoteEmoji))
                signupReaction = reaction;

        if (signupReaction == null) return;

        signupReaction.getUsers().queue(users -> {
            for (int i = 0; i < users.size(); i++) {
                var user = users.get(i);
                if (user.isBot()) {
                    users.remove(user);
                    i--;
                }
            }
            if (users.size() == amount || forceEnd) {
                String lang = internalGuild.getLanguage(guild, author);
                var eb = new EmbedBuilder();
                eb.setColor(new User(internalGuild.getSignupAuthorId(messageId, guild, author)).getColor(guild, author));
                var title = internalGuild.getSignupTitle(messageId, guild, author);
                eb.setTitle(String.format(LanguageHandler.get(lang, "signup_embedtitle"), (title.isEmpty() ? "" : "for "), title));

                eb.setDescription(String.format(LanguageHandler.get(lang, "signup_embeddescriptionend"), amount) + "\n");
                if (users.isEmpty()) eb.appendDescription(LanguageHandler.get(lang, "signup_nobody"));
                else for (var user : users) eb.appendDescription(user.getAsMention() + "\n");
                message.editMessage(eb.build()).queue();
                message.clearReactions().queue();
                internalGuild.unsetSignup(messageId, guild, author);
            }
        });
    }
}
