// Author: Tancred423 (https://github.com/Tancred423)
package moderation.autorole;

import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import moderation.guild.Guild;
import servant.Log;

import java.sql.SQLException;

public class AutoroleListener extends ListenerAdapter {
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        var name = "autorole";
        var eventUser = event.getUser();
        if (eventUser.isBot()) return;

        // Enabled?
        try {
            if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("autorole")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), name, null).sendLog(false);
        }

        Guild internalGuild;
        try {
            internalGuild = new Guild(event.getGuild().getIdLong());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), name, null).sendLog(false);
            return;
        }
        try {
            if (internalGuild.hasAutorole()) {
                event.getGuild().getController().addSingleRoleToMember(event.getMember(), internalGuild.getAutorole()).queue();
            }
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), name, null).sendLog(false);
        }
    }
}
