package listeners;

import files.language.LanguageHandler;
import moderation.guild.Server;
import moderation.log.Log;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import servant.Servant;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class GuildUpdateBoostCountListener extends ListenerAdapter {
    // This event will be thrown if a text channel gets deleted.
    public void onGuildUpdateBoostCount(@Nonnull GuildUpdateBoostCountEvent event) {
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
            var server = new Server(guild);
            var jda = event.getJDA();
            var lang = server.getLanguage();

            // Log
            if (server.getLogChannelId() != 0 && server.logIsEnabled("boost_count")) {
                var logChannel = guild.getTextChannelById(server.getLogChannelId());
                if (logChannel != null) {
                    logChannel.sendMessage(Log.getLogEmbed(jda, Color.decode(Servant.config.getDefaultColorCode()),
                            LanguageHandler.get(lang, "log_boost_count_title"),
                            String.format(LanguageHandler.get(lang, "log_boost_count_description"), event.getNewBoostCount())
                    )).queue();
                }
            }
        }, Servant.fixedThreadPool);
    }
}
