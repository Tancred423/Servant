// Author: Tancred423 (https://github.com/Tancred423)
package moderation.autorole;

import moderation.toggle.Toggle;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import moderation.guild.Guild;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Log;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AutoRoleListener extends ListenerAdapter {
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
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
                        var rolesToAdd = new ArrayList<Role>();
                        rolesToAdd.add(internalGuild.getAutorole(guild, eventUser).entrySet().iterator().next().getKey());
                        guild.modifyMemberRoles(member, rolesToAdd, null).queue();
                    } catch (InterruptedException e) {
                        new Log(e, event.getGuild(), eventUser, "Autorole Listener", null).sendLog(false);
                    } catch (HierarchyException ignored) { }
                });

                delay.start();
            }
        });
    }
}
