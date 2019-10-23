package useful.signup;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;
import servant.Servant;
import utilities.Emote;

import java.awt.*;
import java.sql.SQLException;

public class SignupListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;
        if (!Toggle.isEnabled(event, "signup")) return;

        var guild = event.getGuild();
        var internalGuild = new Guild(guild.getIdLong());
        var messageId = event.getMessageIdLong();

        try {
            if (!internalGuild.isSignupMessage(messageId)) return;
        } catch (SQLException e) {
            new Log(e, guild, event.getUser(), "signup", null).sendLog(false);
            return;
        }

        var forceEnd = false;
        var endEmoji = Emote.getEmoji("end");
        try {
            if (!event.getReactionEmote().isEmote() && event.getReactionEmote().getName().equals(endEmoji)) {
                if (event.getUser().getIdLong() != internalGuild.getSignupAuthorId(event.getMessageIdLong()))
                    event.getReaction().removeReaction(event.getUser()).queue();
                else forceEnd = true;
            }
        } catch (SQLException e) {
            new Log(e, guild, event.getUser(), "signup", null).sendLog(false);
            return;
        }
        var finalForceEnd = forceEnd;
        event.getChannel().getMessageById(messageId).queue(message -> endSignup(internalGuild, messageId, message, guild, event.getUser(), finalForceEnd));
    }

    static void endSignup(Guild internalGuild, long messageId, Message message, net.dv8tion.jda.core.entities.Guild guild, net.dv8tion.jda.core.entities.User author, boolean forceEnd) {
        try {
            if (!internalGuild.isSignupMessage(messageId)) return;
            var amount = internalGuild.getSignupAmount(messageId);
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
                    String lang;
                    try {
                        lang = internalGuild.getLanguage();
                    } catch (SQLException e) {
                        lang = Servant.config.getDefaultLanguage();
                    }
                    var eb = new EmbedBuilder();
                    try {
                        eb.setColor(new User(internalGuild.getSignupAuthorId(messageId)).getColor());
                    } catch (SQLException e) {
                        eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
                    }
                    try {
                        var title = internalGuild.getSignupTitle(messageId);
                        eb.setTitle(String.format(LanguageHandler.get(lang, "signup_embedtitle"), (title.isEmpty() ? "" : "for "), title));

                        eb.setDescription(String.format(LanguageHandler.get(lang, "signup_embeddescriptionend"), amount) + "\n");
                        if (users.isEmpty()) eb.appendDescription(LanguageHandler.get(lang, "signup_nobody"));
                        else for (var user : users) eb.appendDescription(user.getAsMention() + "\n");
                        message.editMessage(eb.build()).queue();
                        message.clearReactions().queue();
                        internalGuild.unsetSignup(messageId);
                    } catch (SQLException e) {
                        new Log(e, guild, author, "signup", null).sendLog(false);
                    }
                }
            });
        } catch (SQLException e) {
            new Log(e, guild, author, "signup", null).sendLog(false);
        }
    }
}
