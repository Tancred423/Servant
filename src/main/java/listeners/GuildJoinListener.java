// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import files.language.LanguageHandler;
import moderation.guild.Server;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.Constants;
import utilities.ImageUtil;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

public class GuildJoinListener extends ListenerAdapter {
    // This event will be thrown if the bot joins a guild.
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
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
        if (Blacklist.isBlacklisted(guild, user)) return;

        CompletableFuture.runAsync(() -> {
            // Invite
            processInvite(event, guild, guildOwner);
        }, Servant.fixedThreadPool);
    }

    private static void processInvite(GuildJoinEvent event, net.dv8tion.jda.api.entities.Guild guild, Member guildOwner) {
        if (guildOwner == null) return;
        var guildOwnerUser = guildOwner.getUser();

        System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " +
                "Servant was invited to " + guild.getName() + " (" + guild.getIdLong() + "). Owner: " + guildOwnerUser + "#" + guildOwnerUser.getDiscriminator() + " (" + guildOwner.getIdLong() + ").");

        if (Blacklist.isBlacklisted(guild, guildOwnerUser)) {
            event.getGuild().leave().queue();
            System.out.println("Servant left " + guild.getName() + " because this guild was blacklisted.");
            return;
        }

        guildOwnerUser.openPrivateChannel().queue(privateChannel -> {
            var guildOwnerMaster = new Master(guildOwnerUser);
            var language = new Server(guild).getLanguage();
            var p = Servant.config.getDefaultPrefix();
            var botOwner = event.getJDA().getUserById(Servant.config.getBotOwnerId());
            if (botOwner == null) return;
            var bot = event.getJDA().getSelfUser();
            var eb = new EmbedBuilder();

            eb.setColor(guildOwnerMaster.getColor());
            eb.setAuthor(String.format(LanguageHandler.get(language, "invite_author"), bot.getName()), null, bot.getEffectiveAvatarUrl());
            eb.setThumbnail(guild.getIconUrl());
            eb.setDescription(String.format(LanguageHandler.get(language, "invite_description"), p, p, p, Servant.config.getSupportGuildInv(), botOwner.getName(), botOwner.getDiscriminator()));
            eb.setImage(ImageUtil.getImageUrl(event.getJDA(), "invite"));
            eb.setFooter(String.format(LanguageHandler.get(language, "invite_footer"), guild.getName()), null);

            privateChannel.sendMessage(eb.build()).queue();
        }, failure -> { /* ignore */ });
    }
}
