// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import servant.MyGuild;
import servant.Servant;
import utilities.Console;
import utilities.Constants;

import java.util.concurrent.CompletableFuture;

public class GuildLeaveListener extends ListenerAdapter {
    // This event will be thrown if the bot joins a guild.
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        var guild = event.getGuild();
        var guildOwner = guild.getOwner();
        if (guildOwner == null) return; // To eliminate errors. Will never occur.
        var user = guildOwner.getUser();

        // Blacklist
        if (guild.getIdLong() == Constants.DISCORD_BOT_LIST_ID) return;
        if (user.isBot()) return;

        CompletableFuture.runAsync(() -> {
            var myGuild = new MyGuild(guild);
            var guildOwnerUser = guildOwner.getUser();

            Console.log("Servant was kicked from " + guild.getName() + " (" + guild.getIdLong() + ") | Guild size: " + guild.getMemberCount() + " | Owner: " + guildOwnerUser + "#" + guildOwnerUser.getDiscriminator() + " (" + guildOwner.getIdLong() + ").");

            // PURGES
            myGuild.purge();
        }, Servant.fixedThreadPool);
    }
}
