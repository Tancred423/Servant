package listeners;

import moderation.guild.Guild;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import owner.blacklist.Blacklist;
import servant.Servant;
import useful.giveaway.Giveaway;

import java.util.concurrent.CompletableFuture;

public class TextChannelDeleteListener extends ListenerAdapter {
    // This event will be thrown if a text channel gets deleted.
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
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
            var internalGuild = new Guild(guild.getIdLong());
            var channel = event.getChannel();

            // Birthday (Auto Updating Lists)
            if (internalGuild.getBirthdayMessageChannelId(guild, user) == channel.getIdLong())
                internalGuild.unsetBirthdayMessage(guild, user);

            // Giveaway
            Giveaway.purgeGiveawaysFromChannel(guild.getIdLong(),channel.getIdLong(), guild, user);

            // MediaOnlyChannel
            if (internalGuild.mediaOnlyChannelHasEntry(channel, guild, user))
                internalGuild.unsetMediaOnlyChannel(channel, guild, user);

            // Signup
            internalGuild.purgeSignupsFromChannel(channel.getIdLong(), guild, user);

            // Poll
            internalGuild.purgePollsFromChannel(channel.getIdLong(), guild, user);
        }, Servant.threadPool);
    }
}
