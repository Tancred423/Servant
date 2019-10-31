// Author: Tancred423 (https://github.com/Tancred423)
package useful.signup;

import moderation.guild.Guild;
import moderation.toggle.Toggle;
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
            event.getChannel().getMessageById(messageId).queue(message -> Signup.endSignup(internalGuild, messageId, message, guild, event.getUser(), finalForceEnd));
        });
    }
}
