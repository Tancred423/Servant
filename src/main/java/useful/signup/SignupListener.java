// Author: Tancred423 (https://github.com/Tancred423)
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
import utilities.Emote;

import java.util.concurrent.CompletableFuture;

public class SignupListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        CompletableFuture.runAsync(() -> {
            if (event.getUser().isBot()) return;
            if (!Toggle.isEnabled(event, "signup")) return;

            var guild = event.getGuild();
            var internalGuild = new Guild(guild.getIdLong());
            var messageId = event.getMessageIdLong();

            if (!internalGuild.isSignupMessage(messageId, guild, event.getUser())) return;

            var forceEnd = false;
            var endEmoji = Emote.getEmoji("end");
            if (!event.getReactionEmote().isEmote() && event.getReactionEmote().getName().equals(endEmoji)) {
                if (event.getUser().getIdLong() != internalGuild.getSignupAuthorId(event.getMessageIdLong(), guild, event.getUser()))
                    event.getReaction().removeReaction(event.getUser()).queue();
                else forceEnd = true;
            }
            var finalForceEnd = forceEnd;
            event.getChannel().getMessageById(messageId).queue(message -> endSignup(internalGuild, messageId, message, guild, event.getUser(), finalForceEnd));
        });
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
