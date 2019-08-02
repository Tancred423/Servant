// Author: Tancred423 (https://github.com/Tancred423)
package moderation.reactionRoles;

import moderation.guild.Guild;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;

import java.sql.SQLException;

public class ReactionRoleListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        // Enabled?
        try {
            if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("reactionrole")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), "reactionrole", null).sendLog(false);
        }

        if (event.getUser().isBot()) return;

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

        try {
            if (internalGuild.hasEntry(guildId, channelId, messageId, emoji, emoteGuildId, emoteId)) {
                long roleId = internalGuild.getRoleId(guildId, channelId, messageId, emoji, emoteGuildId, emoteId);
                try {
                    event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById(roleId)).queue();
                } catch (InsufficientPermissionException e) {
                    event.getChannel().sendMessage("Insufficient Permissions.").queue();
                }
            }
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), "reactionrole", null).sendLog(false);
        }
    }

    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        // Enabled?
        try {
            if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("reactionrole")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), "reactionrole", null).sendLog(false);
        }

        if (event.getUser().isBot()) return;

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

        try {
            if (internalGuild.hasEntry(guildId, channelId, messageId, emoji, emoteGuildId, emoteId)) {
                long roleId = internalGuild.getRoleId(guildId, channelId, messageId, emoji, emoteGuildId, emoteId);
                try {
                    event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), event.getGuild().getRoleById(roleId)).queue();
                } catch (InsufficientPermissionException e) {
                    event.getChannel().sendMessage("Insufficient Permissions.").queue();
                }
            }
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), "reactionrole", null).sendLog(false);
        }
    }
}
