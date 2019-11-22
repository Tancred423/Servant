// Author: Tancred423 (https://github.com/Tancred423)
package moderation.reactionRole;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ReactionRoleListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
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
                    var rolesToAdd = new ArrayList<Role>();
                    rolesToAdd.add(event.getGuild().getRoleById(roleId));
                    event.getGuild().modifyMemberRoles(event.getMember(), rolesToAdd, null).queue();
                } catch (InsufficientPermissionException | HierarchyException e) {
                    event.getChannel().sendMessage(LanguageHandler.get(new Guild(event.getGuild().getIdLong()).getLanguage(guild, user), "reactionrole_insufficient")).queue();
                }
            }
        });
    }

    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
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
                    var rolesToRemove = new ArrayList<Role>();
                    rolesToRemove.add(event.getGuild().getRoleById(roleId));
                    event.getGuild().modifyMemberRoles(event.getMember(), null, rolesToRemove).queue();
                } catch (InsufficientPermissionException | HierarchyException e) {
                    event.getChannel().sendMessage(LanguageHandler.get(new Guild(event.getGuild().getIdLong()).getLanguage(guild, user), "reactionrole_insufficient")).queue();
                }
            }
        });
    }
}
