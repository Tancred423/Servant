// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import files.language.LanguageHandler;
import moderation.guild.Server;
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
import useful.polls.PollsDatabase;
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
        if (user.isBot()) return;
        if (Blacklist.isBlacklisted(user, guild)) return;

        CompletableFuture.runAsync(() -> {
            var server = new Server(guild);
            var messageId = event.getMessageIdLong();

            // Quickpoll
            if (PollsDatabase.isQuickpoll(messageId, guild, user)) {
                processQuickpollMultipleVote(event, guild, user, messageId);
            }

            // Radiopoll
            if (PollsDatabase.isRadioVote(messageId, guild, user)) {
                processRadiovoteMultipleVote(event, guild, user, messageId);
            }

            // Reaction Role
            if (Toggle.isEnabled(event, "reactionrole")) {
                processReactionRole(event, guild, user, server);
            }
        }, Servant.threadPool);
    }

    private static void processQuickpollMultipleVote(GuildMessageReactionRemoveEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, long messageId) {
        // Just react to Upvote, Shrug and Downvote.
        var reactionEmote = event.getReactionEmote();
        if (!reactionEmote.getName().equals(EmoteUtil.getEmoji("upvote"))
                && !reactionEmote.getName().equals(EmoteUtil.getEmoji("shrug"))
                && !reactionEmote.getName().equals(EmoteUtil.getEmoji("downvote")))
            return;

        event.getChannel().retrieveMessageById(messageId).queue(message -> {
            if (reactionEmote.getName().equals(PollsDatabase.getVoteEmoji(messageId, user.getIdLong(), guild, user)))
                PollsDatabase.unsetUserVote(messageId, user.getIdLong(), guild, user);
        });
    }

    private static void processRadiovoteMultipleVote(GuildMessageReactionRemoveEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, long messageId) {
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
                if (reactionEmote.getEmote().getIdLong() == PollsDatabase.getVoteEmoteId(messageId, user.getIdLong(), guild, user))
                    PollsDatabase.unsetUserVote(messageId, user.getIdLong(), guild, user);
            } else {
                if (reactionEmote.getName().equals(PollsDatabase.getVoteEmoji(messageId, user.getIdLong(), guild, user)))
                    PollsDatabase.unsetUserVote(messageId, user.getIdLong(), guild, user);
            }
        });
    }

    private static void processReactionRole(GuildMessageReactionRemoveEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, Server internalGuild) {
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

        if (internalGuild.reactionRoleHasEntry(guildId, channelId, messageId, emoji, emoteGuildId, emoteId)) {
            long roleId = internalGuild.getRoleId(guildId, channelId, messageId, emoji, emoteGuildId, emoteId);
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
