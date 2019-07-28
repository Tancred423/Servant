package vote;

import moderation.guild.Guild;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Emote;
import servant.Servant;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class QuickvoteEndListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        var user = event.getUser();
        if (user.isBot()) return; // No bots.

        var messageId = event.getMessageIdLong();
        try {
            if (!VoteDatabase.isQuickvote(messageId)) return; // Has to be a quickvote.
        } catch (SQLException e) {
            return;
        }

        var reactionEmote = event.getReactionEmote();
        if (reactionEmote.isEmote()) {
            try {
                if (!reactionEmote.getEmote().equals(Emote.getEmote("end"))) return; // Has to be the end emote ...
            } catch (SQLException e) {
                return;
            }
        } else {
            if (!reactionEmote.getName().equals(Emote.getEmoji("end"))) return; // ... or the end emoji.
        }

        try {
            if (user.getIdLong() != VoteDatabase.getAuthorId(messageId)) return; // Has to be done by author.
        } catch (SQLException e) {
            return;
        }

        // The author has reacted with an ending emote on their quickvote.
        event.getChannel().getMessageById(messageId).queue(message -> {
            var upvoteCount = 0;
            var shrugCount = 0;
            var downvoteCount = 0;

            net.dv8tion.jda.core.entities.Emote upvoteEmote;
            net.dv8tion.jda.core.entities.Emote shrugEmote;
            net.dv8tion.jda.core.entities.Emote downvoteEmote;
            try {
                upvoteEmote = Emote.getEmote("upvote");
                shrugEmote = Emote.getEmote("shrug");
                downvoteEmote = Emote.getEmote("downvote");
            } catch (SQLException e) {
                return;
            }

            var upvoteEmoji = Emote.getEmoji("upvote");
            var shrugEmoji = Emote.getEmoji("shrug");
            var downvoteEmoji = Emote.getEmoji("downvote");

            var reactions = message.getReactions();
            for (var reaction : reactions) {
                var emote = reaction.getReactionEmote();

                if (emote.isEmote()) {
                    if (emote.getEmote().equals(upvoteEmote)) upvoteCount = reaction.getCount() - 1;
                    if (emote.getEmote().equals(shrugEmote)) shrugCount = reaction.getCount() - 1;
                    if (emote.getEmote().equals(downvoteEmote)) downvoteCount = reaction.getCount() - 1;
                } else {
                    if (emote.getName().equals(upvoteEmoji)) upvoteCount = reaction.getCount() - 1;
                    if (emote.getName().equals(shrugEmoji)) shrugCount = reaction.getCount() - 1;
                    if (emote.getName().equals(downvoteEmoji)) downvoteCount = reaction.getCount() - 1;
                }
            }

            var messageEmbed = message.getEmbeds().get(0);
            var eb = new EmbedBuilder();
            eb.setColor(messageEmbed.getColor());
            eb.setAuthor(user.getName() + " has ended the quickvote!", null, messageEmbed.getAuthor().getIconUrl());
            eb.setDescription(messageEmbed.getDescription());
            eb.addField((upvoteEmote != null ? upvoteEmote.getAsMention() : upvoteEmoji), String.valueOf(upvoteCount), true);
            eb.addField((shrugEmote != null ? shrugEmote.getAsMention() : shrugEmoji), String.valueOf(shrugCount), true);
            eb.addField((downvoteEmote != null ? downvoteEmote.getAsMention() : downvoteEmoji), String.valueOf(downvoteCount), true);
            eb.setFooter("This vote has ended.", event.getJDA().getSelfUser().getAvatarUrl());
            try {
                eb.setTimestamp(OffsetDateTime.now(ZoneId.of(new Guild(event.getGuild().getIdLong()).getOffset())));
            } catch (SQLException e) {
                eb.setTimestamp(OffsetDateTime.now(ZoneId.of(Servant.config.getDefaultOffset())).getOffset());
            }

            message.editMessage(eb.build()).queue();
            message.clearReactions().queue();
            try {
                VoteDatabase.unsetQuickvote(message.getIdLong());
                VoteDatabase.unsetUserVotes(message.getIdLong());
            } catch (SQLException e) {
                event.getJDA().getUserById(Servant.config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                        privateChannel.sendMessage("Greetings master! I couldn't remove a succesful quickvote ending from the database."
                        ).queue());
            }
        });
    }
}
