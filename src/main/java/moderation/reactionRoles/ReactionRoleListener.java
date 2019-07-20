package moderation.reactionRoles;

import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;

import java.sql.SQLException;

public class ReactionRoleListener extends ListenerAdapter {
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        // Enabled?
        try {
            if (!new servant.Guild(event.getGuild().getIdLong()).getToggleStatus("reactionrole")) return;
        } catch (SQLException e) {
            new Log(e, event, "reactionrole").sendLogSqlGuildMessageReactionAddEvent();
        }

        if (event.getUser().isBot()) return;

        long guildId = event.getGuild().getIdLong();
        long channelId = event.getChannel().getIdLong();
        long messageId = event.getMessageIdLong();
        String emoji = null;
        long emoteGuildId = 0;
        long emoteId = 0;

        MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
        if (reactionEmote.isEmote()) {
            if (reactionEmote.getEmote().getGuild() == null) return;
            emoteGuildId = reactionEmote.getEmote().getGuild().getIdLong();
            emoteId = reactionEmote.getEmote().getIdLong();
        } else {
            emoji = reactionEmote.getName();
        }

        try {
            if (ReactionRoleDatabase.hasEntry(guildId, channelId, messageId, emoji, emoteGuildId, emoteId)) {
                long roleId = ReactionRoleDatabase.getRoleId(guildId, channelId, messageId, emoji, emoteGuildId, emoteId);
                event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById(roleId)).queue();
            }
        } catch (SQLException e) {
            new Log(e, event, "reactionrole").sendLogSqlGuildMessageReactionAddEvent();
        }
    }

    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        // Enabled?
        try {
            if (!new servant.Guild(event.getGuild().getIdLong()).getToggleStatus("reactionrole")) return;
        } catch (SQLException e) {
            new Log(e, event, "reactionrole").sendLogSqlGuildMessageReactionRemoveEvent();
        }

        if (event.getUser().isBot()) return;

        long guildId = event.getGuild().getIdLong();
        long channelId = event.getChannel().getIdLong();
        long messageId = event.getMessageIdLong();
        String emoji = null;
        long emoteGuildId = 0;
        long emoteId = 0;

        MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
        if (reactionEmote.isEmote()) {
            emoteGuildId = reactionEmote.getEmote().getGuild().getIdLong();
            emoteId = reactionEmote.getEmote().getIdLong();
        } else {
            emoji = reactionEmote.getName();
        }

        try {
            if (ReactionRoleDatabase.hasEntry(guildId, channelId, messageId, emoji, emoteGuildId, emoteId)) {
                long roleId = ReactionRoleDatabase.getRoleId(guildId, channelId, messageId, emoji, emoteGuildId, emoteId);
                event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), event.getGuild().getRoleById(roleId)).queue();
            }
        } catch (SQLException e) {
            new Log(e, event, "reactionrole").sendLogSqlGuildMessageReactionRemoveEvent();
        }
    }
}
