// Author: Tancred423 (https://github.com/Tancred423)
package moderation.joinLeaveNotify;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;

import java.sql.SQLException;

public class JoinLeaveNotifyListener extends ListenerAdapter {
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        // Enabled?
        try {
            if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus("join")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), "join", null).sendLog(false);
        }

        var joinedUser = event.getUser();
        var guild = event.getGuild();
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());

        MessageChannel channel;
        try {
            channel = internalGuild.getJoinNotifierChannel();
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), "join", null).sendLog(false);
            return;
        }

        if (channel == null) return;

        channel.sendMessage(joinedUser.getName() + "#" + joinedUser.getDiscriminator() + " just joined " + guild.getName() + "!").queue();
    }

    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        // Enabled?
        try {
            if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus("join")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), "join", null).sendLog(false);
        }

        var leftUser = event.getUser();
        var guild = event.getGuild();
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());

        MessageChannel channel;
        try {
            channel = internalGuild.getJoinNotifierChannel();
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), "join", null).sendLog(false);
            return;
        }

        if (channel == null) return;

        channel.sendMessage(leftUser.getName() + "#" + leftUser.getDiscriminator() + " just left " + guild.getName() + "!").queue();
    }
}
