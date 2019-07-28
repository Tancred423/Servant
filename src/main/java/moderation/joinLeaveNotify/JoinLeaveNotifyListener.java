package moderation.joinLeaveNotify;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
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
            new Log(e, event, "join").sendLogSqlGuildMemberJoinEvent();
        }

        User joinedUser = event.getUser();
        Guild guild = event.getGuild();
        moderation.guild.Guild internalGuild;
        try {
            internalGuild = new moderation.guild.Guild(guild.getIdLong());
        } catch (SQLException e) {
            new Log(e, event, "join").sendLogSqlGuildMemberJoinEvent();
            return;
        }

        MessageChannel channel;
        try {
            channel = internalGuild.getJoinNotifierChannel();
        } catch (SQLException e) {
            new Log(e, event, "join").sendLogSqlGuildMemberJoinEvent();
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
            new Log(e, event, "join").sendLogSqlGuildMemberJoinEvent();
        }

        User leftUser = event.getUser();
        Guild guild = event.getGuild();
        moderation.guild.Guild internalGuild;
        try {
            internalGuild = new moderation.guild.Guild(guild.getIdLong());
        } catch (SQLException e) {
            new Log(e, event, "join").sendLogSqlGuildMemberLeaveEvent();
            return;
        }

        MessageChannel channel;
        try {
            channel = internalGuild.getJoinNotifierChannel();
        } catch (SQLException e) {
            new Log(e, event, "join").sendLogSqlGuildMemberLeaveEvent();
            return;
        }

        if (channel == null) return;

        channel.sendMessage(leftUser.getName() + "#" + leftUser.getDiscriminator() + " just left " + guild.getName() + "!").queue();
    }
}
