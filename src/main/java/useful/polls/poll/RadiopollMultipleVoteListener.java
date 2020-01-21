// Author: Tancred423 (https://github.com/Tancred423)
package useful.polls.poll;

import moderation.toggle.Toggle;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import useful.polls.PollsDatabase;
import utilities.Emote;

import java.util.concurrent.CompletableFuture;

public class RadiopollMultipleVoteListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "radiovote")) return;
            var guild = event.getGuild();
            var user = event.getUser();
            if (user.isBot()) return;

            var messageId = event.getMessageIdLong();
            if (!PollsDatabase.isRadioVote(messageId, guild, user)) return; // Has to be a radiovote.

            // Just react to One - Ten.
            var reactionEmote = event.getReactionEmote();
            if (reactionEmote.isEmote()) {
                if (!reactionEmote.getEmote().equals(Emote.getEmote("one", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("two", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("three", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("four", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("five", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("six", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("seven", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("eight", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("nine", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("ten", guild, user))
                ) return;
            } else {
                if (!reactionEmote.getName().equals(Emote.getEmoji("one"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("two"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("three"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("four"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("five"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("six"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("seven"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("eight"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("nine"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("ten"))
                ) return;
            }

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
            if (!PollsDatabase.isRadioVote(messageId, guild, user)) return; // Has to be a radiovote.

            // Just react to Upvote, Shrug and Downvote.
            var reactionEmote = event.getReactionEmote();
            if (reactionEmote.isEmote()) {
                if (!reactionEmote.getEmote().equals(Emote.getEmote("one", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("two", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("three", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("four", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("five", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("six", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("seven", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("eight", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("nine", guild, user))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("ten", guild, user))
                ) return;
            } else {
                if (!reactionEmote.getName().equals(Emote.getEmoji("one"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("two"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("three"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("four"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("five"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("six"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("seven"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("eight"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("nine"))
                        && !reactionEmote.getName().equals(Emote.getEmoji("ten"))
                ) return;
            }

            event.getChannel().retrieveMessageById(messageId).queue(message -> {
                if (reactionEmote.isEmote()) {
                    if (reactionEmote.getEmote().getIdLong() == PollsDatabase.getVoteEmoteId(messageId, user.getIdLong(), guild, user))
                        PollsDatabase.unsetUserVote(messageId, user.getIdLong(), guild, user);
                } else {
                    if (reactionEmote.getName().equals(PollsDatabase.getVoteEmoji(messageId, user.getIdLong(), guild, user)))
                        PollsDatabase.unsetUserVote(messageId, user.getIdLong(), guild, user);
                }
            });
        });
    }
}
