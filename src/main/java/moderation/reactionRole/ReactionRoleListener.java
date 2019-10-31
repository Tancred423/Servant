// Author: Tancred423 (https://github.com/Tancred423)
package moderation.reactionRole;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.concurrent.CompletableFuture;

public class ReactionRoleListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        CompletableFuture.runAsync(() -> {
            if (event.getUser().isBot()) return;
            if (!Toggle.isEnabled(event, "reactionrole")) return;

            var guild = event.getGuild();
            var user = event.getUser();
            var guildId = event.getGuild().getIdLong();
            var channelId = event.getChannel().getIdLong();
            var messageId = event.getMessageIdLong();
            String emoji = null;
            var emoteGuildId = 0L;
            var emoteId = 0L;
            var internalGuild = new Guild(event.getGuild().getIdLong());

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
                    event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById(roleId)).queue();
                } catch (InsufficientPermissionException | HierarchyException e) {
                    event.getChannel().sendMessage(LanguageHandler.get(new Guild(event.getGuild().getIdLong()).getLanguage(guild, user), "reactionrole_insufficient")).queue();
                }
            }
        });
    }

    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "reactionrole")) return;

            if (event.getUser().isBot()) return;

            var guild = event.getGuild();
            var user = event.getUser();
            var guildId = event.getGuild().getIdLong();
            var channelId = event.getChannel().getIdLong();
            var messageId = event.getMessageIdLong();
            String emoji = null;
            var emoteGuildId = 0L;
            var emoteId = 0L;
            var internalGuild = new Guild(event.getGuild().getIdLong());

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
                    event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), event.getGuild().getRoleById(roleId)).queue();
                } catch (InsufficientPermissionException | HierarchyException e) {
                    event.getChannel().sendMessage(LanguageHandler.get(new Guild(event.getGuild().getIdLong()).getLanguage(guild, user), "reactionrole_insufficient")).queue();
                }
            }
        });
    }
}
