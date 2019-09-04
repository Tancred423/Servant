// Author: Tancred423 (https://github.com/Tancred423)
package useful.votes.quickvote;

import moderation.toggle.Toggle;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import useful.votes.VotesDatabase;
import utilities.Emote;

import java.sql.SQLException;

public class QuickvoteMultipleVoteListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (!Toggle.isEnabled(event, "quickvote")) return;
        var user = event.getUser();
        if (user.isBot()) return;

        var messageId = event.getMessageIdLong();
        try {
            if (!VotesDatabase.isQuickvote(messageId)) return; // Has to be a quickvote.
        } catch (SQLException e) {
            return;
        }

        // Just react to Upvote, Shrug and Downvote.
        var reactionEmote = event.getReactionEmote();
        if (reactionEmote.isEmote()) {
            try {
                if (!reactionEmote.getEmote().equals(Emote.getEmote("upvote"))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("shrug"))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("downvote"))
                ) return;
            } catch (SQLException e) {
                return;
            }
        } else {
            if (!reactionEmote.getName().equals(Emote.getEmoji("upvote"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("shrug"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("downvote"))) return;
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
    }

    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        var user = event.getUser();
        if (user.isBot()) return;

        var messageId = event.getMessageIdLong();
        try {
            if (!VotesDatabase.isQuickvote(messageId)) return; // Has to be a quickvote.
        } catch (SQLException e) {
            return;
        }

        // Just react to Upvote, Shrug and Downvote.
        var reactionEmote = event.getReactionEmote();
        if (reactionEmote.isEmote()) {
            try {
                if (!reactionEmote.getEmote().equals(Emote.getEmote("upvote"))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("shrug"))
                        && !reactionEmote.getEmote().equals(Emote.getEmote("downvote"))
                ) return;
            } catch (SQLException e) {
                return;
            }
        } else {
            if (!reactionEmote.getName().equals(Emote.getEmoji("upvote"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("shrug"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("downvote"))
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
    }

    private void unsetVote(long messageId, User user) {
        try {
            VotesDatabase.unsetUserVote(messageId, user.getIdLong());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
