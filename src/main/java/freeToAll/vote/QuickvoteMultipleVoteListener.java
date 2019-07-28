package freeToAll.vote;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Emote;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class QuickvoteMultipleVoteListener extends ListenerAdapter {
    private Map<User, Boolean> tmp = new HashMap<>();

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        User user = event.getUser();
        if (user.isBot()) return;

        var messageId = event.getMessageIdLong();
        try {
            if (!VoteDatabase.isQuickvote(messageId)) return; // Has to be a quickvote.
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
            long userId = user.getIdLong();
            try {
                if (VoteDatabase.hasVoted(messageId, userId)){
                    tmp.put(user, true);
                    event.getReaction().removeReaction(user).queue();
                }
                else VoteDatabase.setUserVote(messageId, userId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        User user = event.getUser();
        if (user.isBot()) return;

        if (tmp.get(user)) {
            tmp.replace(user, false);
            return;
        }

        var messageId = event.getMessageIdLong();
        try {
            if (!VoteDatabase.isQuickvote(messageId)) return; // Has to be a quickvote.
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
            long userId = user.getIdLong();
            try {
                VoteDatabase.unsetUserVote(messageId, userId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
