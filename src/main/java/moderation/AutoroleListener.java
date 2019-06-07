package moderation;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Guild;
import servant.Log;

import java.sql.SQLException;

public class AutoroleListener extends ListenerAdapter {
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        String name = "autorole";
        User eventUser = event.getUser();
        if (eventUser.isBot()) return;

        Guild internalGuild = new Guild(event.getGuild().getIdLong());
        try {
            if (internalGuild.hasAutorole()) {
                event.getGuild().getController().addSingleRoleToMember(event.getMember(), internalGuild.getAutorole()).queue();
            }
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlMemberJoinEvent();
        }
    }
}
