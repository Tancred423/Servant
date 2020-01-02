// Author: Tancred423 (https://github.com/Tancred423)
package useful.polls.poll;

import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import useful.polls.Poll;
import useful.polls.PollsDatabase;
import utilities.Emote;

import java.util.concurrent.CompletableFuture;

public class PollEndListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "vote")) return;

            var guild = event.getGuild();
            var user = event.getUser();
            var lang = new Guild(event.getGuild().getIdLong()).getLanguage(guild, user);

            if (user.isBot()) return;

            var messageId = event.getMessageIdLong();
            if (!PollsDatabase.isVote(messageId, guild, user) && !PollsDatabase.isRadioVote(messageId, guild, user)) return; // Has to be a poll or radiopoll.

            var reactionEmote = event.getReactionEmote();
            if (!reactionEmote.isEmote()) {
                if (!reactionEmote.getName().equals(Emote.getEmoji("end"))) return;
            } else return;

            if (user.getIdLong() != PollsDatabase.getAuthorId(messageId, guild, user)) return; // Has to be done by author.

            // The author has reacted with an ending emote on their poll.
            event.getChannel().retrieveMessageById(messageId).queue(message -> Poll.endPoll(guild, user, message, lang, event.getJDA()));
        });
    }


}
