// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import files.language.LanguageHandler;
import moderation.guild.Server;
import moderation.reactionRole.ReactionRole;
import moderation.toggle.Toggle;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Servant;
import useful.polls.Poll;
import utilities.EmoteUtil;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class GuildMessageReactionRemoveListener extends ListenerAdapter {
    // This event will be thrown if a user removes their reaction from a message in a guild.
    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
        var guild = event.getGuild();
        var user = event.getUser();

        /* Certain conditions must meet, so this event is allowed to be executed:
         * 1.   Ignore any request from the Discord Bot List as this big guild
         *      invoke a lot of events, but never use this bot actively.
         * 2.   Ignore any request from bots to prevent infinite loops.
         * 3.   Ignore any request from blacklisted users and guilds.
         */
        if (guild.getIdLong() == 264445053596991498L) return; // Discord Bot List
        if (user == null) return;
        if (user.isBot()) return;
        if (Blacklist.isBlacklisted(guild, user)) return;

        CompletableFuture.runAsync(() -> {
            var jda = event.getJDA();
            var server = new Server(guild);
            var channel = event.getChannel();
            var messageId = event.getMessageIdLong();
            var lang = server.getLanguage();

            channel.retrieveMessageById(messageId).queue(message -> {
                var poll = new Poll(jda, lang, message);

                // Quickpoll
                if (poll.isQuickPoll()) processQuickpollMultipleVote(event, poll, user, messageId);

                // Radiopoll
                if (poll.isRadioPoll()) processRadiovoteMultipleVote(event, poll, user, messageId);
            }, f -> {});

            // Reaction Role
            if (Toggle.isEnabled(event, "reactionrole")) {
                processReactionRole(event, guild, server);
            }
        }, Servant.fixedThreadPool);
    }

    private static void processQuickpollMultipleVote(GuildMessageReactionRemoveEvent event, Poll poll, User user, long messageId) {
        // Just react to Upvote, Shrug and Downvote.
        var reactionEmote = event.getReactionEmote();
        if (!reactionEmote.getName().equals(EmoteUtil.getEmoji("upvote"))
                && !reactionEmote.getName().equals(EmoteUtil.getEmoji("shrug"))
                && !reactionEmote.getName().equals(EmoteUtil.getEmoji("downvote")))
            return;

        event.getChannel().retrieveMessageById(messageId).queue(message -> {
            if (reactionEmote.getName().equals(poll.getVoteEmoji(user.getIdLong())))
                poll.unsetVote(user.getIdLong());
        });
    }

    private static void processRadiovoteMultipleVote(GuildMessageReactionRemoveEvent event, Poll poll, User user, long messageId) {
        // Just react to Upvote, Shrug and Downvote.
        var reactionEmote = event.getReactionEmote();
        if (!reactionEmote.isEmote()) {
            if (!reactionEmote.getName().equals(EmoteUtil.getEmoji("one"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("two"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("three"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("four"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("five"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("six"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("seven"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("eight"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("nine"))
                    && !reactionEmote.getName().equals(EmoteUtil.getEmoji("ten"))
            ) return;
        } else return;

        event.getChannel().retrieveMessageById(messageId).queue(message -> {
            if (reactionEmote.isEmote()) {
                if (reactionEmote.getEmote().getIdLong() == poll.getVoteEmoteId(user.getIdLong()))
                    poll.unsetVote(user.getIdLong());
            } else {
                if (reactionEmote.getName().equals(poll.getVoteEmoji(user.getIdLong())))
                    poll.unsetVote(user.getIdLong());
            }
        });
    }

    private static void processReactionRole(GuildMessageReactionRemoveEvent event, net.dv8tion.jda.api.entities.Guild guild, Server server) {
        var guildId = guild.getIdLong();
        var channelId = event.getChannel().getIdLong();
        var messageId = event.getMessageIdLong();
        String emoji = null;
        var emoteGuildId = 0L;
        var emoteId = 0L;

        var reactionEmote = event.getReactionEmote();
        if (reactionEmote.isEmote()) {
            if (reactionEmote.getEmote().getGuild() == null) return;
            emoteGuildId = reactionEmote.getEmote().getGuild().getIdLong();
            emoteId = reactionEmote.getEmote().getIdLong();
        } else {
            emoji = reactionEmote.getName();
        }

        var reactionRole = new ReactionRole(event.getJDA(), guildId, channelId, messageId, emoji, emoteGuildId, emoteId);

        if (reactionRole.hasEntry()) {
            long roleId = reactionRole.getRoleId();
            try {
                var rolesToRemove = new ArrayList<Role>();
                rolesToRemove.add(event.getGuild().getRoleById(roleId));
                event.getGuild().modifyMemberRoles(event.getMember(), null, rolesToRemove).queue();
            } catch (InsufficientPermissionException | HierarchyException e) {
                event.getChannel().sendMessage(LanguageHandler.get(new Server(event.getGuild()).getLanguage(), "reactionrole_insufficient")).queue();
            }
        }
    }
}
