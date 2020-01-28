package listeners;

import moderation.guild.Server;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Servant;
import useful.giveaway.GiveawayHandler;

import java.util.concurrent.CompletableFuture;

public class GuildMessageDeleteListener extends ListenerAdapter {
    // This event will be thrown if a messages gets deleted on a guild.
    public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event) {
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
        if (Blacklist.isBlacklisted(user, guild)) return;

        CompletableFuture.runAsync(() -> {
            var server = new Server(guild);
            var messageId = event.getMessageIdLong();

            // Birthday
            if (server.getBirthdayMessageMessageId() == messageId)
                server.unsetBirthdayMessage();

            // Giveaway
            if (GiveawayHandler.isGiveaway(guild.getIdLong(), event.getChannel().getIdLong(), event.getMessageIdLong(), guild, user))
                GiveawayHandler.deleteGiveawayFromDb(guild.getIdLong(), event.getChannel().getIdLong(), messageId, guild, user);

            // Signup
            if (server.isSignupMessage(messageId))
                server.unsetSignup(messageId);

            // Poll
            if (server.isPoll(messageId))
                server.unsetPoll(messageId);
        }, Servant.threadPool);
    }
}
