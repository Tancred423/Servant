// Author: Tancred423 (https://github.com/Tancred423)
package moderation.autorole;

import moderation.toggle.Toggle;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import moderation.guild.Guild;
import owner.blacklist.Blacklist;
import servant.Log;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AutoroleListener extends ListenerAdapter {
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            var eventUser = event.getUser();
            if (eventUser.isBot()) return;
            if (!Toggle.isEnabled(event, "autorole")) return;

            var internalGuild = new Guild(event.getGuild().getIdLong());
            if (internalGuild.hasAutorole(guild, eventUser)) {
            if (Blacklist.isBlacklisted(event.getUser(), event.getGuild())) return;
                Thread delay = new Thread(() -> {
                    try {
                        var roleAndDelay = internalGuild.getAutorole(guild, eventUser);
                        TimeUnit.MINUTES.sleep(roleAndDelay.entrySet().iterator().next().getValue());
                        var member = event.getMember();
                        guild.getController().addSingleRoleToMember(member, internalGuild.getAutorole(guild, eventUser).entrySet().iterator().next().getKey()).queue();
                    } catch (InterruptedException e) {
                        new Log(e, event.getGuild(), eventUser, "Autorole Listener", null).sendLog(false);
                    } catch (HierarchyException ignored) { }
                });

                delay.start();
            }
        });
    }
}
