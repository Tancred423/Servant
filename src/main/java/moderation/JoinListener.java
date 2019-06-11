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
        User joinedUser = event.getUser();
        Guild guild = event.getGuild();
        servant.Guild internalGuild = new servant.Guild(guild.getIdLong());

        MessageChannel channel;
        try {
            channel = internalGuild.getJoinNotifierChannel();
        } catch (SQLException e) {
            new Log(e, event, "join").sendLogSqlCommandEvent(true);
            return;
        }

        if (channel == null) return;

        channel.sendMessage(joinedUser.getAsMention() + " just joined " + guild.getName() + "!").queue();
    }

    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        User leftUser = event.getUser();
        Guild guild = event.getGuild();
        servant.Guild internalGuild = new servant.Guild(guild.getIdLong());

        MessageChannel channel;
        try {
            channel = internalGuild.getJoinNotifierChannel();
        } catch (SQLException e) {
            new Log(e, event, "join").sendLogSqlCommandEvent(true);
            return;
        }

        if (channel == null) return;

        channel.sendMessage(leftUser.getAsMention() + " just left " + guild.getName() + "!").queue();
    }
}
