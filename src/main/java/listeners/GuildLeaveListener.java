// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import servant.MyGuild;
import servant.Servant;

import java.util.concurrent.CompletableFuture;

public class GuildLeaveListener extends ListenerAdapter {
    // This event will be thrown if the bot joins a guild.
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        var guild = event.getGuild();
        var guildOwner = guild.getOwner();
        if (guildOwner == null) return; // To eliminate errors. Will never occur.
        var user = guildOwner.getUser();

        /* Certain conditions must meet, so this event is allowed to be executed:
         * 1.   Ignore any request from the Discord Bot List as this big guild
         *      invoke a lot of events, but never use this bot actively.
         * 2.   Ignore any request from bots to prevent infinite loops.
         * 3.   Ignore any request from blacklisted users and guilds.
         */
        if (guild.getIdLong() == 264445053596991498L) return; // Discord Bot List
        if (user.isBot()) return;

        CompletableFuture.runAsync(() -> {
            var myGuild = new MyGuild(guild);

            // PURGES
            myGuild.purge();
        }, Servant.fixedThreadPool);
    }
}
