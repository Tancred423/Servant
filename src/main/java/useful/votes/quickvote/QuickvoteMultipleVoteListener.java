// Author: Tancred423 (https://github.com/Tancred423)
package useful.votes.quickvote;

import moderation.toggle.Toggle;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import useful.votes.VotesDatabase;
import utilities.Emote;

import java.util.concurrent.CompletableFuture;

public class QuickvoteMultipleVoteListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "quickvote")) return;
            var guild = event.getGuild();
            var user = event.getUser();
            if (user.isBot()) return;

            var messageId = event.getMessageIdLong();
            if (!VotesDatabase.isQuickvote(messageId, guild, user)) return; // Has to be a quickvote.

            // Just react to Upvote, Shrug and Downvote.
            var reactionEmote = event.getReactionEmote();
            if (!reactionEmote.getName().equals(Emote.getEmoji("upvote"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("shrug"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("downvote"))) return;


            event.getChannel().retrieveMessageById(messageId).queue(message -> {
                var userId = user.getIdLong();
                if (VotesDatabase.hasVoted(messageId, userId, guild, user)) event.getReaction().removeReaction(user).queue();
                else VotesDatabase.setUserVote(messageId, userId, (reactionEmote.isEmote() ? reactionEmote.getEmote().getIdLong() : 0), (reactionEmote.isEmote() ? "" : reactionEmote.getName()), guild, user);
            });
        });
    }

    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            var user = event.getUser();
            if (user.isBot()) return;

            var messageId = event.getMessageIdLong();
            if (!VotesDatabase.isQuickvote(messageId, guild, user)) return; // Has to be a quickvote.

            // Just react to Upvote, Shrug and Downvote.
            var reactionEmote = event.getReactionEmote();
            if (!reactionEmote.getName().equals(Emote.getEmoji("upvote"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("shrug"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("downvote")))
                return;


            event.getChannel().retrieveMessageById(messageId).queue(message -> {
                if (reactionEmote.getName().equals(VotesDatabase.getVoteEmoji(messageId, user.getIdLong(), guild, user)))
                    unsetVote(messageId, user, guild);
            });
        });
    }

    private void unsetVote(long messageId, User user, Guild guild) {
        VotesDatabase.unsetUserVote(messageId, user.getIdLong(), guild, user);
    }
}
