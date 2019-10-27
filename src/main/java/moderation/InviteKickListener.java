// Author: Tancred423 (https://github.com/Tancred423)
package moderation;

import owner.blacklist.Blacklist;
import utilities.Image;
import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;
import servant.Servant;

import java.awt.*;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class InviteKickListener extends ListenerAdapter {
    public void onGuildJoin(GuildJoinEvent event) {
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            var guildOwner = guild.getOwner().getUser();

            System.out.println("Servant was invited to " + guild.getName() + " (" + guild.getIdLong() + "). Owner: " + guildOwner.getName() + "#" + guildOwner.getDiscriminator() + " (" + guildOwner.getIdLong() + ").");

            if (Blacklist.isBlacklisted(guildOwner, guild)) {
                event.getGuild().leave().queue();
                System.out.println("Servant left " + guild.getName() + " because this guild was blacklisted.");
                return;
            }

            guildOwner.openPrivateChannel().queue(privateChannel -> {
                var internalGuildOwner = new User(guildOwner.getIdLong());
                String language;
                try {
                    language = new Guild(guild.getIdLong()).getLanguage();
                } catch (SQLException e) {
                    new Log(e, guild, guildOwner, "invite", null).sendLog(false);
                    return;
                }
                var p = Servant.config.getDefaultPrefix();
                var botOwner = Servant.jda.getUserById(Servant.config.getBotOwnerId());
                var bot = event.getJDA().getSelfUser();
                var eb = new EmbedBuilder();

                try {
                    eb.setColor(internalGuildOwner.getColor());
                } catch (SQLException e) {
                    eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
                }

                eb.setAuthor(String.format(LanguageHandler.get(language, "invite_author"), bot.getName()), null, guild.getIconUrl());
                eb.setDescription(String.format(LanguageHandler.get(language, "invite_description"), p, p, p, Servant.config.getSupportGuildInv(), botOwner.getName(), botOwner.getDiscriminator()));
                eb.setImage(Image.getImageUrl("invite"));
                eb.setFooter(String.format(LanguageHandler.get(language, "invite_footer"), guild.getName()), null);

                privateChannel.sendMessage(eb.build()).queue();
            });
        });
    }

    public void onGuildLeave(GuildLeaveEvent event) {
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            var guildOwner = guild.getOwner().getUser();

            if (Blacklist.isBlacklisted(guildOwner, guild)) return;

            System.out.println("Servant was kicked from " + guild.getName() + " (" + guild.getIdLong() + "). Owner: " + guildOwner.getName() + "#" + guildOwner.getDiscriminator() + " (" + guildOwner.getIdLong() + ").");

            guildOwner.openPrivateChannel().queue(privateChannel -> {
                        var internalGuildOwner = new User(guildOwner.getIdLong());
                        String language;
                        try {
                            language = new Guild(guild.getIdLong()).getLanguage();
                        } catch (SQLException e) {
                            new Log(e, guild, guildOwner, "invite", null).sendLog(false);
                            return;
                        }
                        var botOwner = Servant.jda.getUserById(Servant.config.getBotOwnerId());
                        var eb = new EmbedBuilder();

                        try {
                            eb.setColor(internalGuildOwner.getColor());
                        } catch (SQLException e) {
                            eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
                        }
                        eb.setAuthor(LanguageHandler.get(language, "kick_author"), null, guild.getIconUrl());
                        eb.setDescription(String.format(LanguageHandler.get(language, "kick_description"), Servant.config.getSupportGuildInv(), botOwner.getName(), botOwner.getDiscriminator()));
                        eb.setImage(Image.getImageUrl("kick"));
                        eb.setFooter(String.format(LanguageHandler.get(language, "kick_footer"), guild.getName()), null);

                        privateChannel.sendMessage(eb.build()).queue();
                    },
                    fail -> System.out.println("Couldn't send guild owner DM after kick."));
        });
    }
}
