// Author: Tancred423 (https://github.com/Tancred423)
package useful.votes.quickvote;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import useful.votes.VotesDatabase;
import utilities.Emote;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

public class QuickvoteEndListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "quickvote")) return;

            var guild = event.getGuild();
            var user = event.getUser();
            var lang = new Guild(event.getGuild().getIdLong()).getLanguage(guild, user);

            if (user.isBot()) return;

            var messageId = event.getMessageIdLong();
            if (!VotesDatabase.isQuickvote(messageId, guild, user)) return; // Has to be a quickvote.

            var reactionEmote = event.getReactionEmote();
            if (reactionEmote.isEmote()) {
                if (!reactionEmote.getEmote().equals(Emote.getEmote("end", guild, user))) return; // Has to be the end emote ...
            } else {
                if (!reactionEmote.getName().equals(Emote.getEmoji("end"))) return; // ... or the end emoji.
            }

            if (user.getIdLong() != VotesDatabase.getAuthorId(messageId, guild, user)) return; // Has to be done by author.

            // The author has reacted with an ending emote on their quickvote.
            event.getChannel().retrieveMessageById(messageId).queue(message -> {
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
                var author = messageEmbed.getAuthor();
                if (author == null) return; // todo: always null?

                var eb = new EmbedBuilder();
                eb.setColor(messageEmbed.getColor());
                eb.setAuthor(String.format(LanguageHandler.get(lang, "quickvote_ended"), user.getName()), null, author.getIconUrl());
                eb.setDescription(messageEmbed.getDescription());
                eb.addField(upvoteEmoji, String.valueOf(upvoteCount), true);
                eb.addField(downvoteEmoji, String.valueOf(downvoteCount), true);
                eb.setFooter(LanguageHandler.get(lang, "votes_inactive"), event.getJDA().getSelfUser().getAvatarUrl());
                eb.setTimestamp(OffsetDateTime.now(ZoneId.of(new Guild(event.getGuild().getIdLong()).getOffset(guild, user))));

                message.editMessage(eb.build()).queue();
                message.clearReactions().queue();
                VotesDatabase.unsetVote(message.getIdLong(), guild, user);
                VotesDatabase.unsetUserVotes(message.getIdLong(), guild, user);
            });
        });
    }
}
