// Author: Tancred423 (https://github.com/Tancred423)
package listeners;

import commands.owner.blacklist.Blacklist;
import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Console;
import utilities.Constants;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class GuildJoinListener extends ListenerAdapter {
    // This event will be thrown if the bot joins a guild.
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        var guild = event.getGuild();
        var guildOwner = guild.getOwner();
        if (guildOwner == null) return; // To eliminate errors. Will never occur.
        var user = guildOwner.getUser();

        // Blacklist
        if (guild.getIdLong() == Constants.DISCORD_BOT_LIST_ID) return;
        if (user.isBot()) return;
        if (Blacklist.isBlacklisted(guild, user)) return;

        CompletableFuture.runAsync(() -> {
            // Invite
            processInvite(event, guild, guildOwner);
        }, Servant.fixedThreadPool);
    }

    private static void processInvite(GuildJoinEvent event, net.dv8tion.jda.api.entities.Guild guild, Member guildOwner) {
        var guildOwnerUser = guildOwner.getUser();

        Console.log("Servant was invited to " + guild.getName() + " (" + guild.getIdLong() + ") | Guild size: " + guild.getMemberCount() + " | Owner: " + guildOwnerUser + "#" + guildOwnerUser.getDiscriminator() + " (" + guildOwner.getIdLong() + ").");

        if (Blacklist.isBlacklisted(guild, guildOwnerUser)) {
            event.getGuild().leave().queue();
            System.out.println("Servant left " + guild.getName() + " because this guild was blacklisted.");
            return;
        }

        guildOwnerUser.openPrivateChannel().queue(privateChannel -> {
            var myOwner = new MyUser(guildOwnerUser);
            var language = new MyGuild(guild).getLanguageCode();
            var botOwner = event.getJDA().getUserById(Servant.config.getBotOwnerId());
            if (botOwner == null) return;
            var bot = event.getJDA().getSelfUser();
            var eb = new EmbedBuilder();

            eb.setColor(Color.decode(myOwner.getColorCode()));
            eb.setAuthor(String.format(LanguageHandler.get(language, "invite_author"), bot.getName()), null, bot.getEffectiveAvatarUrl());
            eb.setThumbnail(guild.getIconUrl());
            eb.setDescription(String.format(LanguageHandler.get(language, "invite_description"), Constants.WEBSITE, Constants.SUPPORT));
            eb.setFooter(String.format(LanguageHandler.get(language, "invite_footer"), guild.getName()), null);

            privateChannel.sendMessage(eb.build()).queue();
        }, failure -> { /* ignore */ });
    }
}
