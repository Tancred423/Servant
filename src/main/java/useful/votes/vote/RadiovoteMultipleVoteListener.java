// Author: Tancred423 (https://github.com/Tancred423)
package useful.votes.vote;

import moderation.toggle.Toggle;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import useful.votes.VotesDatabase;
import utilities.Emote;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class RadiovoteMultipleVoteListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "radiovote")) return;
            var user = event.getUser();
            if (user.isBot()) return;

            var messageId = event.getMessageIdLong();
            try {
                if (!VotesDatabase.isRadioVote(messageId)) return; // Has to be a radiovote.
            } catch (SQLException e) {
                return;
            }

            // Just react to One - Ten.
            var reactionEmote = event.getReactionEmote();
            if (reactionEmote.isEmote()) {
                try {
                    if (!reactionEmote.getEmote().equals(Emote.getEmote("one"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("two"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("three"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("four"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("five"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("six"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("seven"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("eight"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("nine"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("ten"))
                    ) return;
                } catch (SQLException e) {
                    return;
                }
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

            event.getChannel().getMessageById(messageId).queue(message -> {
                var userId = user.getIdLong();
                try {
                    if (VotesDatabase.hasVoted(messageId, userId)) event.getReaction().removeReaction(user).queue();
                    else VotesDatabase.setUserVote(messageId, userId, (reactionEmote.isEmote() ? reactionEmote.getEmote().getIdLong() : 0), (reactionEmote.isEmote() ? "" : reactionEmote.getName()));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        CompletableFuture.runAsync(() -> {
            var user = event.getUser();
            if (user.isBot()) return;

            var messageId = event.getMessageIdLong();
            try {
                if (!VotesDatabase.isRadioVote(messageId)) return; // Has to be a radiovote.
            } catch (SQLException e) {
                return;
            }

            // Just react to Upvote, Shrug and Downvote.
            var reactionEmote = event.getReactionEmote();
            if (reactionEmote.isEmote()) {
                try {
                    if (!reactionEmote.getEmote().equals(Emote.getEmote("one"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("two"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("three"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("four"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("five"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("six"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("seven"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("eight"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("nine"))
                            && !reactionEmote.getEmote().equals(Emote.getEmote("ten"))
                    ) return;
                } catch (SQLException e) {
                    return;
                }
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

            event.getChannel().getMessageById(messageId).queue(message -> {
                if (reactionEmote.isEmote()) {
                    try {
                        if (reactionEmote.getEmote().getIdLong() == VotesDatabase.getVoteEmoteId(messageId, user.getIdLong()))
                            unsetVote(messageId, user);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        if (reactionEmote.getName().equals(VotesDatabase.getVoteEmoji(messageId, user.getIdLong())))
                            unsetVote(messageId, user);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    private void unsetVote(long messageId, User user) {
        try {
            VotesDatabase.unsetUserVote(messageId, user.getIdLong());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
