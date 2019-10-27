// Author: Tancred423 (https://github.com/Tancred423)
package useful.votes.quickvote;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;
import useful.votes.VotesDatabase;
import utilities.Emote;
import servant.Servant;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

public class QuickvoteEndListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "quickvote")) return;

            String lang;
            try {
                lang = new Guild(event.getGuild().getIdLong()).getLanguage();
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getUser(), "quickvote", null).sendLog(false);
                return;
            }

            var user = event.getUser();
            if (user.isBot()) return;

            var messageId = event.getMessageIdLong();
            try {
                if (!VotesDatabase.isQuickvote(messageId)) return; // Has to be a quickvote.
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
                if (user.getIdLong() != VotesDatabase.getAuthorId(messageId)) return; // Has to be done by author.
            } catch (SQLException e) {
                return;
            }

            // The author has reacted with an ending emote on their quickvote.
            event.getChannel().getMessageById(messageId).queue(message -> {
                var upvoteCount = 0;
                var downvoteCount = 0;

                var upvoteEmoji = Emote.getEmoji("upvote");
                var downvoteEmoji = Emote.getEmoji("downvote");

                var reactions = message.getReactions();
                for (var reaction : reactions) {
                    var emote = reaction.getReactionEmote();
                    if (emote.getName().equals(upvoteEmoji)) upvoteCount = reaction.getCount() - 1;
                    if (emote.getName().equals(downvoteEmoji)) downvoteCount = reaction.getCount() - 1;
                }

                var messageEmbed = message.getEmbeds().get(0);
                var eb = new EmbedBuilder();
                eb.setColor(messageEmbed.getColor());
                eb.setAuthor(String.format(LanguageHandler.get(lang, "quickvote_ended"), user.getName()), null, messageEmbed.getAuthor().getIconUrl());
                eb.setDescription(messageEmbed.getDescription());
                eb.addField(upvoteEmoji, String.valueOf(upvoteCount), true);
                eb.addField(downvoteEmoji, String.valueOf(downvoteCount), true);
                eb.setFooter(LanguageHandler.get(lang, "votes_inactive"), event.getJDA().getSelfUser().getAvatarUrl());
                try {
                    eb.setTimestamp(OffsetDateTime.now(ZoneId.of(new Guild(event.getGuild().getIdLong()).getOffset())));
                } catch (SQLException e) {
                    eb.setTimestamp(OffsetDateTime.now(ZoneId.of(Servant.config.getDefaultOffset())).getOffset());
                }

                message.editMessage(eb.build()).queue();
                message.clearReactions().queue();
                try {
                    VotesDatabase.unsetVote(message.getIdLong());
                    VotesDatabase.unsetUserVotes(message.getIdLong());
                } catch (SQLException e) {
                    event.getJDA().getUserById(Servant.config.getBotOwnerId()).openPrivateChannel().queue(privateChannel ->
                            privateChannel.sendMessage(LanguageHandler.get(lang, "quickvote_missing_db")
                            ).queue());
                }
            });
        });
    }
}
