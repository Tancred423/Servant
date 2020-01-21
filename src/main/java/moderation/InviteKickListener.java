// Author: Tancred423 (https://github.com/Tancred423)
package moderation;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Servant;
import utilities.Constants;
import utilities.Image;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

public class InviteKickListener extends ListenerAdapter {
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                var guild = event.getGuild();
                var guildOwner = guild.getOwner();
                if (guildOwner == null) return;
                var guildOwnerUser = guildOwner.getUser();

                System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " +
                        "Servant was invited to " + guild.getName() + " (" + guild.getIdLong() + "). Owner: " + guildOwnerUser + "#" + guildOwnerUser.getDiscriminator() + " (" + guildOwner.getIdLong() + ").");

                if (Blacklist.isBlacklisted(guildOwnerUser, guild)) {
                    event.getGuild().leave().queue();
                    System.out.println("Servant left " + guild.getName() + " because this guild was blacklisted.");
                    return;
                }

                guildOwnerUser.openPrivateChannel().queue(privateChannel -> {
                    var internalGuildOwner = new User(guildOwnerUser.getIdLong());
                    var language = new Guild(guild.getIdLong()).getLanguage(guild, guildOwnerUser);
                    var p = Servant.config.getDefaultPrefix();
                    var botOwner = event.getJDA().getUserById(Servant.config.getBotOwnerId());
                    if (botOwner == null) return;
                    var bot = event.getJDA().getSelfUser();
                    var eb = new EmbedBuilder();

                    eb.setColor(internalGuildOwner.getColor(guild, guildOwnerUser));
                    eb.setAuthor(String.format(LanguageHandler.get(language, "invite_author"), bot.getName()), null, guild.getIconUrl());
                    eb.setDescription(String.format(LanguageHandler.get(language, "invite_description"), p, p, p, Servant.config.getSupportGuildInv(), botOwner.getName(), botOwner.getDiscriminator()));
                    eb.setImage(Image.getImageUrl("invite", guild, guildOwnerUser));
                    eb.setFooter(String.format(LanguageHandler.get(language, "invite_footer"), guild.getName()), null);

                    privateChannel.sendMessage(eb.build()).queue();
                }, failure -> { /* ignore */ });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            var guildOwner = guild.getOwner();
            if (guildOwner == null) return;
            var guildOwnerUser = guildOwner.getUser();

            if (Blacklist.isBlacklisted(guildOwnerUser, guild)) return;

            System.out.println("[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] " +
                    "Servant was kicked from " + guild.getName() + " (" + guild.getIdLong() + "). Owner: " + guildOwnerUser.getName() + "#" + guildOwnerUser.getDiscriminator() + " (" + guildOwner.getIdLong() + ").");

            guildOwnerUser.openPrivateChannel().queue(privateChannel -> {
                        var internalGuildOwner = new User(guildOwnerUser.getIdLong());
                        String language;
                        language = new Guild(guild.getIdLong()).getLanguage(guild, guildOwnerUser);
                        var botOwner = event.getJDA().getUserById(Servant.config.getBotOwnerId());
                        if (botOwner == null) return;
                        var eb = new EmbedBuilder();

                        eb.setColor(internalGuildOwner.getColor(guild, guildOwnerUser));
                        eb.setAuthor(LanguageHandler.get(language, "kick_author"), null, guild.getIconUrl());
                        eb.setDescription(String.format(LanguageHandler.get(language, "kick_description"), Servant.config.getSupportGuildInv(), botOwner.getName(), botOwner.getDiscriminator()));
                        eb.setImage(Image.getImageUrl("kick", guild, guildOwnerUser));
                        eb.setFooter(String.format(LanguageHandler.get(language, "kick_footer"), guild.getName()), null);

                        privateChannel.sendMessage(eb.build()).queue();
                    }, failure -> { /* ignore */ });
        });
    }
}
