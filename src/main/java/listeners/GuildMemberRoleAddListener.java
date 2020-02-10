package listeners;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import patreon.PatreonHandler;
import servant.Servant;

import java.util.concurrent.CompletableFuture;

public class GuildMemberRoleAddListener extends ListenerAdapter {
    // This event will be thrown if a user reacts on a message in a guild.
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        var guild = event.getGuild();
        var user = event.getUser();

        /* Certain conditions must meet, so this event is allowed to be executed:
         * 1.   Ignore any request from the Discord Bot List as this big guild
         *      invoke a lot of events, but never use this bot actively.
         * 2.   Ignore any request from bots to prevent infinite loops.
         * 3.   Ignore any request from blacklisted users and guilds.
         */
        if (guild.getIdLong() == 264445053596991498L) return; // Discord Bot List
        if (user.isBot()) return;
        if (Blacklist.isBlacklisted(guild, user)) return;

        CompletableFuture.runAsync(() -> {
            // Patreon
            processPatreon(event);
        }, Servant.fixedThreadPool);
    }

    private static void processPatreon(GuildMemberRoleAddEvent event) {
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
    }
}
