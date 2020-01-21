// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import files.language.LanguageHandler;
import moderation.guild.Guild;
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
import utilities.Emote;

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
            var internalGuild = new Guild(guild.getIdLong());
            var messageId = event.getMessageIdLong();
            var lang = new Guild(event.getGuild().getIdLong()).getLanguage(guild, user);

            // Quickpoll
            if (PollsDatabase.isQuickvote(messageId, guild, user)) {
                processQuickpollMultipleVote(event, guild, user, messageId);
            }

            // Radiopoll
            if (PollsDatabase.isRadioVote(messageId, guild, user)) {
                processRadiovoteMultipleVote(event, guild, user, messageId);
            }

            // Reaction Role
            if (Toggle.isEnabled(event, "reactionrole")) {
                processReactionRole(event, guild, user, internalGuild);
            }
        }, Servant.threadPool);
    }

    private static void processQuickpollMultipleVote(GuildMessageReactionRemoveEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, long messageId) {
        // Just react to Upvote, Shrug and Downvote.
        var reactionEmote = event.getReactionEmote();
        if (!reactionEmote.getName().equals(Emote.getEmoji("upvote"))
                && !reactionEmote.getName().equals(Emote.getEmoji("shrug"))
                && !reactionEmote.getName().equals(Emote.getEmoji("downvote")))
            return;

        event.getChannel().retrieveMessageById(messageId).queue(message -> {
            if (reactionEmote.getName().equals(PollsDatabase.getVoteEmoji(messageId, user.getIdLong(), guild, user)))
                PollsDatabase.unsetUserVote(messageId, user.getIdLong(), guild, user);
        });
    }

    private static void processRadiovoteMultipleVote(GuildMessageReactionRemoveEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, long messageId) {
        // Just react to Upvote, Shrug and Downvote.
        var reactionEmote = event.getReactionEmote();
        if (reactionEmote.isEmote()) {
            if (!reactionEmote.getEmote().equals(Emote.getEmote("one", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("two", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("three", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("four", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("five", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("six", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("seven", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("eight", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("nine", guild, user))
                    && !reactionEmote.getEmote().equals(Emote.getEmote("ten", guild, user))
            ) return;
        } else {
            if (!reactionEmote.getName().equals(Emote.getEmoji("one"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("two"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("three"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("four"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("five"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("six"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("seven"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("eight"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("nine"))
                    && !reactionEmote.getName().equals(Emote.getEmoji("ten"))
            ) return;
        }

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

    private static void processReactionRole(GuildMessageReactionRemoveEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, Guild internalGuild) {
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

        if (internalGuild.reactionRoleHasEntry(guildId, channelId, messageId, emoji, emoteGuildId, emoteId, guild, user)) {
            long roleId = internalGuild.getRoleId(guildId, channelId, messageId, emoji, emoteGuildId, emoteId, guild, user);
            try {
                var rolesToRemove = new ArrayList<Role>();
                rolesToRemove.add(event.getGuild().getRoleById(roleId));
                event.getGuild().modifyMemberRoles(event.getMember(), null, rolesToRemove).queue();
            } catch (InsufficientPermissionException | HierarchyException e) {
                event.getChannel().sendMessage(LanguageHandler.get(new Guild(event.getGuild().getIdLong()).getLanguage(guild, user), "reactionrole_insufficient")).queue();
            }
        }
    }
}
