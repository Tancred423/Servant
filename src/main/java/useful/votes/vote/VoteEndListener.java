// Author: Tancred423 (https://github.com/Tancred423)
package useful.votes.vote;

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
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class VoteEndListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "vote")) return;

            var guild = event.getGuild();
            var user = event.getUser();
            var lang = new Guild(event.getGuild().getIdLong()).getLanguage(guild, user);

            if (user.isBot()) return;

            var messageId = event.getMessageIdLong();
            if (!VotesDatabase.isVote(messageId, guild, user) && !VotesDatabase.isRadioVote(messageId, guild, user)) return; // Has to be a vote or radiovote.

            var reactionEmote = event.getReactionEmote();
            if (!reactionEmote.isEmote()) {
                if (!reactionEmote.getName().equals(Emote.getEmoji("end"))) return;
            } else return;

            if (user.getIdLong() != VotesDatabase.getAuthorId(messageId, guild, user)) return; // Has to be done by author.

            // The author has reacted with an ending emote on their quickvote.
            event.getChannel().retrieveMessageById(messageId).queue(message -> {
                Map<Integer, Integer> count = new HashMap<>();
                for (int i = 0; i < 10; i++) count.put(i + 1, 0);

                net.dv8tion.jda.api.entities.Emote oneEmote;
                net.dv8tion.jda.api.entities.Emote twoEmote;
                net.dv8tion.jda.api.entities.Emote threeEmote;
                net.dv8tion.jda.api.entities.Emote fourEmote;
                net.dv8tion.jda.api.entities.Emote fiveEmote;
                net.dv8tion.jda.api.entities.Emote sixEmote;
                net.dv8tion.jda.api.entities.Emote sevenEmote;
                net.dv8tion.jda.api.entities.Emote eightEmote;
                net.dv8tion.jda.api.entities.Emote nineEmote;
                net.dv8tion.jda.api.entities.Emote tenEmote;

                oneEmote = Emote.getEmote("one", guild, user);
                twoEmote = Emote.getEmote("two", guild, user);
                threeEmote = Emote.getEmote("three", guild, user);
                fourEmote = Emote.getEmote("four", guild, user);
                fiveEmote = Emote.getEmote("five", guild, user);
                sixEmote = Emote.getEmote("six", guild, user);
                sevenEmote = Emote.getEmote("seven", guild, user);
                eightEmote = Emote.getEmote("eight", guild, user);
                nineEmote = Emote.getEmote("nine", guild, user);
                tenEmote = Emote.getEmote("ten", guild, user);

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
                var description = messageEmbed.getDescription();
                List<String> lines = new ArrayList<>();
                if (description != null) lines = Arrays.asList(description.split("\\r?\\n"));

                var author = messageEmbed.getAuthor();
                if (author == null) return; // todo: always null?

                eb.setColor(messageEmbed.getColor());
                eb.setAuthor(String.format(LanguageHandler.get(lang, "vote_ended"), user.getName()), null, author.getIconUrl());
                eb.setTitle(messageEmbed.getTitle());
                for (int i = 0; i < lines.size(); i++) eb.addField(lines.get(i), String.valueOf(count.get(i + 1)), true);
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
