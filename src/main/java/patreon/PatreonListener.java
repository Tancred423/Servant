// Author: Tancred423 (https://github.com/Tancred423)
package patreon;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import servant.Servant;

import java.util.concurrent.CompletableFuture;

public class PatreonListener extends ListenerAdapter {
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        if (event.getGuild().getIdLong() == 264445053596991498L) return; // Discord Bot List
        CompletableFuture.runAsync(() -> {
            if (event.getMember().getUser().isBot()) return;
            if (!event.getGuild().getId().equals(Servant.config.getSupportGuildId())) return;

            switch (event.getRoles().get(0).getId()) {
                case "489738762838867969": // Donation
                    PatreonHandler.sendPatreonNotification(event, "donation");
                    break;
                case "502472440455233547": // $1
                    PatreonHandler.sendPatreonNotification(event, "$1");
                    break;
                case "502472546600353796": // $3
                    PatreonHandler.sendPatreonNotification(event, "$3");
                    break;
                case "502472823638458380": // $5
                    PatreonHandler.sendPatreonNotification(event, "$5");
                    break;
                case "502472869234868224": // $10
                    PatreonHandler.sendPatreonNotification(event, "$10");
                    break;
            }
        });
    }
}
