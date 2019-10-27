// Author: Tancred423 (https://github.com/Tancred423)
package useful.votes.vote;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;
import servant.Servant;
import useful.votes.VotesDatabase;
import utilities.Emote;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class VoteEndListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "vote")) return;

            String lang;
            try {
                lang = new Guild(event.getGuild().getIdLong()).getLanguage();
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getUser(), "vote", null).sendLog(false);
                return;
            }

            var user = event.getUser();
            if (user.isBot()) return;

            var messageId = event.getMessageIdLong();
            try {
                if (!VotesDatabase.isVote(messageId) && !VotesDatabase.isRadioVote(messageId)) return; // Has to be a vote or radiovote.
            } catch (SQLException e) {
                return;
            }

            var reactionEmote = event.getReactionEmote();
            if (!reactionEmote.isEmote()) {
                if (!reactionEmote.getName().equals(Emote.getEmoji("end"))) return;
            } else return;

            try {
                if (user.getIdLong() != VotesDatabase.getAuthorId(messageId)) return; // Has to be done by author.
            } catch (SQLException e) {
                return;
            }

            // The author has reacted with an ending emote on their quickvote.
            event.getChannel().getMessageById(messageId).queue(message -> {
                Map<Integer, Integer> count = new HashMap<>();
                for (int i = 0; i < 10; i++) count.put(i + 1, 0);

                net.dv8tion.jda.core.entities.Emote oneEmote;
                net.dv8tion.jda.core.entities.Emote twoEmote;
                net.dv8tion.jda.core.entities.Emote threeEmote;
                net.dv8tion.jda.core.entities.Emote fourEmote;
                net.dv8tion.jda.core.entities.Emote fiveEmote;
                net.dv8tion.jda.core.entities.Emote sixEmote;
                net.dv8tion.jda.core.entities.Emote sevenEmote;
                net.dv8tion.jda.core.entities.Emote eightEmote;
                net.dv8tion.jda.core.entities.Emote nineEmote;
                net.dv8tion.jda.core.entities.Emote tenEmote;

                try {
                    oneEmote = Emote.getEmote("one");
                    twoEmote = Emote.getEmote("two");
                    threeEmote = Emote.getEmote("three");
                    fourEmote = Emote.getEmote("four");
                    fiveEmote = Emote.getEmote("five");
                    sixEmote = Emote.getEmote("six");
                    sevenEmote = Emote.getEmote("seven");
                    eightEmote = Emote.getEmote("eight");
                    nineEmote = Emote.getEmote("nine");
                    tenEmote = Emote.getEmote("ten");
                } catch (SQLException e) {
                    return;
                }

                var oneEmoji = Emote.getEmoji("one");
                var twoEmoji = Emote.getEmoji("two");
                var threeEmoji = Emote.getEmoji("three");
                var fourEmoji = Emote.getEmoji("four");
                var fiveEmoji = Emote.getEmoji("five");
                var sixEmoji = Emote.getEmoji("six");
                var sevenEmoji = Emote.getEmoji("seven");
                var eightEmoji = Emote.getEmoji("eight");
                var nineEmoji = Emote.getEmoji("nine");
                var tenEmoji = Emote.getEmoji("ten");

                var reactions = message.getReactions();
                for (var reaction : reactions) {
                    var emote = reaction.getReactionEmote();

                    if (emote.isEmote()) {
                        if (emote.getEmote().equals(oneEmote)) count.put(1, reaction.getCount() - 1);
                        if (emote.getEmote().equals(twoEmote)) count.put(2, reaction.getCount() - 1);
                        if (emote.getEmote().equals(threeEmote)) count.put(3, reaction.getCount() - 1);
                        if (emote.getEmote().equals(fourEmote)) count.put(4, reaction.getCount() - 1);
                        if (emote.getEmote().equals(fiveEmote)) count.put(5, reaction.getCount() - 1);
                        if (emote.getEmote().equals(sixEmote)) count.put(6, reaction.getCount() - 1);
                        if (emote.getEmote().equals(sevenEmote)) count.put(7, reaction.getCount() - 1);
                        if (emote.getEmote().equals(eightEmote)) count.put(8, reaction.getCount() - 1);
                        if (emote.getEmote().equals(nineEmote)) count.put(9, reaction.getCount() - 1);
                        if (emote.getEmote().equals(tenEmote)) count.put(10, reaction.getCount() - 1);
                    } else {
                        if (emote.getName().equals(oneEmoji)) count.put(1, reaction.getCount() - 1);
                        if (emote.getName().equals(twoEmoji)) count.put(2, reaction.getCount() - 1);
                        if (emote.getName().equals(threeEmoji)) count.put(3, reaction.getCount() - 1);
                        if (emote.getName().equals(fourEmoji)) count.put(4, reaction.getCount() - 1);
                        if (emote.getName().equals(fiveEmoji)) count.put(5, reaction.getCount() - 1);
                        if (emote.getName().equals(sixEmoji)) count.put(6, reaction.getCount() - 1);
                        if (emote.getName().equals(sevenEmoji)) count.put(7, reaction.getCount() - 1);
                        if (emote.getName().equals(eightEmoji)) count.put(8, reaction.getCount() - 1);
                        if (emote.getName().equals(nineEmoji)) count.put(9, reaction.getCount() - 1);
                        if (emote.getName().equals(tenEmoji)) count.put(10, reaction.getCount() - 1);
                    }
                }

                var messageEmbed = message.getEmbeds().get(0);
                var eb = new EmbedBuilder();
                List<String> lines = Arrays.asList(messageEmbed.getDescription().split("\\r?\\n"));

                eb.setColor(messageEmbed.getColor());
                eb.setAuthor(String.format(LanguageHandler.get(lang, "vote_ended"), user.getName()), null, messageEmbed.getAuthor().getIconUrl());
                eb.setTitle(messageEmbed.getTitle());
                for (int i = 0; i < lines.size(); i++) eb.addField(lines.get(i), String.valueOf(count.get(i + 1)), true);
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
                            privateChannel.sendMessage(LanguageHandler.get(lang, "vote_missing_db")
                            ).queue());
                }
            });
        });
    }
}
