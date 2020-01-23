// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Servant;
import useful.giveaway.Giveaway;
import utilities.Constants;
import utilities.Image;

import java.time.OffsetDateTime;
import java.time.ZoneId;
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
        if (Blacklist.isBlacklisted(user, guild)) return;

        CompletableFuture.runAsync(() -> {
            var internalGuild = new Guild(guild.getIdLong());

            // Birthday
            internalGuild.purgeBirthday(guild, guildOwner.getUser());

            // Giveaway
            Giveaway.purgeGiveaways(guild.getIdLong(), guild, user);

            // Kick
            processKick(event, guild, guildOwner, user);

            // MediaOnlyChannel
            internalGuild.purgeMediaOnlyChannels(guild, user);

            // Signup
            internalGuild.purgeSignups(guild, user);

            // Poll
            internalGuild.purgePolls(guild, user);
        }, Servant.threadPool);
    }

    private static void processKick(GuildLeaveEvent event, net.dv8tion.jda.api.entities.Guild guild, Member guildOwner, net.dv8tion.jda.api.entities.User guildOwnerUser) {
        System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " +
                "Servant was kicked from " + guild.getName() + " (" + guild.getIdLong() + "). Owner: " + guildOwnerUser.getName() + "#" + guildOwnerUser.getDiscriminator() + " (" + guildOwner.getIdLong() + ").");

        guildOwnerUser.openPrivateChannel().queue(privateChannel -> {
            var internalGuildOwner = new User(guildOwnerUser.getIdLong());
            var language = new Guild(guild.getIdLong()).getLanguage(guild, guildOwnerUser);
            var botOwner = event.getJDA().getUserById(Servant.config.getBotOwnerId());
            if (botOwner == null) return;
            var eb = new EmbedBuilder();

            eb.setColor(internalGuildOwner.getColor(guild, guildOwnerUser));
            eb.setAuthor(LanguageHandler.get(language, "kick_author"), null, guild.getIconUrl());
            eb.setDescription(String.format(LanguageHandler.get(language, "kick_description"), Servant.config.getSupportGuildInv(), botOwner.getName(), botOwner.getDiscriminator()));
            eb.setImage(Image.getImageUrl("kick", guild, guildOwnerUser));
            eb.setFooter(String.format(LanguageHandler.get(language, "kick_footer"), guild.getName()), null);

            privateChannel.sendMessage(eb.build()).queue(success -> { /* ignore */ }, failure -> { /* ignore */ });
        }, failure -> { /* ignore */ });
    }
}
