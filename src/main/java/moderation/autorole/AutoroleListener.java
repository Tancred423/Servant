// Author: Tancred423 (https://github.com/Tancred423)
package moderation.autorole;

import moderation.toggle.Toggle;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import moderation.guild.Guild;
import owner.blacklist.Blacklist;
import servant.Log;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class AutoroleListener extends ListenerAdapter {
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        var name = "autorole";
        var eventUser = event.getUser();
        if (eventUser.isBot()) return;
        if (!Toggle.isEnabled(event, name)) return;

        var internalGuild = new Guild(event.getGuild().getIdLong());
        try {
            if (internalGuild.hasAutorole()) {
//                if (Blacklist.isBlacklisted(event.getUser(), event.getGuild())) return;
                Thread delay = new Thread(() -> {
                    try {
                        var roleAndDelay = internalGuild.getAutorole();
                        TimeUnit.MINUTES.sleep(roleAndDelay.entrySet().iterator().next().getValue());
                        var guild = event.getGuild();
                        var member = event.getMember();
                        guild.getController().addSingleRoleToMember(member, internalGuild.getAutorole().entrySet().iterator().next().getKey()).queue();
                    } catch (SQLException | InterruptedException e) {
                        new Log(e, event.getGuild(), eventUser, "Autorole Listener", null).sendLog(false);
                    } catch (HierarchyException ignored) { }
                });

                delay.start();
            }
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getUser(), name, null).sendLog(false);
        }
    }
}
