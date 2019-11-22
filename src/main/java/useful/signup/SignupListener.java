// Author: Tancred423 (https://github.com/Tancred423)
package useful.signup;

import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import utilities.Emote;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

public class SignupListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        CompletableFuture.runAsync(() -> {
            if (event.getUser().isBot()) return;
            if (!Toggle.isEnabled(event, "signup")) return;

            var user = event.getUser();
            var guild = event.getGuild();
            var internalGuild = new Guild(guild.getIdLong());
            var messageId = event.getMessageIdLong();

            if (!internalGuild.isSignupMessage(messageId, guild, event.getUser())) return;

            var forceEnd = false;
            var endEmoji = Emote.getEmoji("end");
            if (!event.getReactionEmote().isEmote() && event.getReactionEmote().getName().equals(endEmoji)) {
                if (event.getUser().getIdLong() != internalGuild.getSignupAuthorId(event.getMessageIdLong(), guild, user))
                    event.getReaction().removeReaction(user).queue();
                else forceEnd = true;
            }

            var expiration = internalGuild.getSignupTime(messageId, guild, user);
            var isCustomDate = internalGuild.signupIsCustomDate(messageId, guild, user);
            if (!isCustomDate) expiration = ZonedDateTime.now(ZoneOffset.of(internalGuild.getOffset(guild, user)));

            var finalForceEnd = forceEnd;
            var finalExpiration = expiration;
            event.getChannel().retrieveMessageById(messageId).queue(message -> Signup.endSignup(internalGuild, messageId, message, guild, event.getUser(), finalForceEnd, finalExpiration));
        });
    }
}
