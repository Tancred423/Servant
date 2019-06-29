package moderation;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;

import java.sql.SQLException;

public class JoinListener extends ListenerAdapter {
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        // Enabled?
        try {
            if (!new servant.Guild(event.getGuild().getIdLong()).getToggleStatus("join")) return;
        } catch (SQLException e) {
            new Log(e, event, "join").sendLogSqlMemberJoinEvent();
        }

        User joinedUser = event.getUser();
        Guild guild = event.getGuild();
        servant.Guild internalGuild;
        try {
            internalGuild = new servant.Guild(guild.getIdLong());
        } catch (SQLException e) {
            new Log(e, event, "join").sendLogSqlMemberJoinEvent();
            return;
        }

        MessageChannel channel;
        try {
            channel = internalGuild.getJoinNotifierChannel();
        } catch (SQLException e) {
            new Log(e, event, "join").sendLogSqlMemberJoinEvent();
            return;
        }

        if (channel == null) return;

        channel.sendMessage(joinedUser.getName() + "#" + joinedUser.getDiscriminator() + " just joined " + guild.getName() + "!").queue();
    }

    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        // Enabled?
        try {
            if (!new servant.Guild(event.getGuild().getIdLong()).getToggleStatus("join")) return;
        } catch (SQLException e) {
            new Log(e, event, "join").sendLogSqlMemberJoinEvent();
        }

        User leftUser = event.getUser();
        Guild guild = event.getGuild();
        servant.Guild internalGuild;
        try {
            internalGuild = new servant.Guild(guild.getIdLong());
        } catch (SQLException e) {
            new Log(e, event, "join").sendLogSqlMemberLeaveEvent();
            return;
        }

        MessageChannel channel;
        try {
            channel = internalGuild.getJoinNotifierChannel();
        } catch (SQLException e) {
            new Log(e, event, "join").sendLogSqlMemberLeaveEvent();
            return;
        }

        if (channel == null) return;

        channel.sendMessage(leftUser.getName() + "#" + leftUser.getDiscriminator() + " just left " + guild.getName() + "!").queue();
    }
}
