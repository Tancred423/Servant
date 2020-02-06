package useful.signup;

import files.language.LanguageHandler;
import moderation.guild.Server;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import servant.Log;
import servant.Servant;
import useful.giveaway.GiveawayHandler;
import utilities.EmoteUtil;
import utilities.ImageUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static servant.Database.closeQuietly;

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
                        if (guild == null) return;
                        var expiration = resultSet.getTimestamp("time").toLocalDateTime()
                                .atZone(ZoneId.of(new Server(guild).getOffset()));
                        var isCustomDate = resultSet.getBoolean("is_custom_date");
                        if (isCustomDate) expiration = expiration.minusMinutes(30);
                        var author = jda.getUserById(resultSet.getLong("author_id"));
                        var now = ZonedDateTime.now(ZoneOffset.of(new Server(guild).getOffset()));
                        var remainingTimeMillis = GiveawayHandler.zonedDateTimeDifference(now, expiration);

                        if (remainingTimeMillis <= 0) {
                            var internalGuild = new Server(guild);
                            var messageId = resultSet.getLong("message_id");
                            var finalExpiration = isCustomDate ? expiration.plusMinutes(30) : expiration;
                            var tc = guild.getTextChannelById(resultSet.getLong("channel_id"));
                            if (tc != null) tc.retrieveMessageById(messageId).queue(message ->
                                    endSignup(internalGuild, messageId, message, guild, author, true, finalExpiration));
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

    public static void endSignup(Server server, long messageId, Message message, net.dv8tion.jda.api.entities.Guild guild,
                                 net.dv8tion.jda.api.entities.User author, boolean forceEnd, ZonedDateTime expiration) {
        if (!server.isSignupMessage(messageId)) return;
        var amount = server.getSignupAmount(messageId);
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
                var signupUser = message.getJDA().getUserById(server.getSignupAuthorId(messageId));
                if (signupUser == null) return;
                eb.setColor(new Master(signupUser).getColor());
                var title = server.getSignupTitle(messageId);
                eb.setTitle(title.isEmpty() ? LanguageHandler.get(lang, "signup_embedtitle_empty") :
                        String.format(LanguageHandler.get(lang, "signup_embedtitle_notempty"), title));
                eb.setDescription(String.format(LanguageHandler.get(lang, "signup_embeddescriptionend"), amount) + "\n");
                if (users.isEmpty()) eb.appendDescription(LanguageHandler.get(lang, "signup_nobody"));
                else for (var user : users) eb.appendDescription(user.getAsMention() + "\n");
                eb.setFooter(server.signupIsCustomDate(messageId) ?
                                LanguageHandler.get(lang, "signup_event") :
                                LanguageHandler.get(lang, "signup_timeout_finish"),
                        ImageUtil.getImageUrl("clock", guild, author));
                eb.setTimestamp(expiration.toInstant());
                message.editMessage(eb.build()).queue();
                message.clearReactions().queue();
                server.unsetSignup(messageId);
            }
        });
    }
}
