// Author: Tancred423 (https://github.com/Tancred423)
package useful.polls.quickpoll;

import moderation.toggle.Toggle;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import useful.polls.PollsDatabase;
import utilities.Emote;

import java.util.concurrent.CompletableFuture;

public class QuickpollMultipleVoteListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "quickvote")) return;
            var guild = event.getGuild();
            var user = event.getUser();
            if (user.isBot()) return;

            var messageId = event.getMessageIdLong();
            if (!PollsDatabase.isQuickvote(messageId, guild, user)) return; // Has to be a quickvote.

            // Just react to Upvote, Shrug and Downvote.
            var reactionEmote = event.getReactionEmote();
            if (!reactionEmote.getName().equals(Emote.getEmoji("upvote"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("shrug"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("downvote"))) return;


            event.getChannel().retrieveMessageById(messageId).queue(message -> {
                var userId = user.getIdLong();
                if (PollsDatabase.hasVoted(messageId, userId, guild, user)) event.getReaction().removeReaction(user).queue();
                else PollsDatabase.setUserVote(messageId, userId, (reactionEmote.isEmote() ? reactionEmote.getEmote().getIdLong() : 0), (reactionEmote.isEmote() ? "" : reactionEmote.getName()), guild, user);
            });
        });
    }

    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            var user = event.getUser();
            if (user.isBot()) return;

            var messageId = event.getMessageIdLong();
            if (!PollsDatabase.isQuickvote(messageId, guild, user)) return; // Has to be a quickvote.

            // Just react to Upvote, Shrug and Downvote.
            var reactionEmote = event.getReactionEmote();
            if (!reactionEmote.getName().equals(Emote.getEmoji("upvote"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("shrug"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("downvote")))
                return;


            event.getChannel().retrieveMessageById(messageId).queue(message -> {
                if (reactionEmote.getName().equals(PollsDatabase.getVoteEmoji(messageId, user.getIdLong(), guild, user)))
                    unsetVote(messageId, user, guild);
            });
        });
    }

    private void unsetVote(long messageId, User user, Guild guild) {
        PollsDatabase.unsetUserVote(messageId, user.getIdLong(), guild, user);
    }
}
