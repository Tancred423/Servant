package moderation;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Servant;

import java.awt.*;
import java.sql.SQLException;

public class InviteKickListener extends ListenerAdapter {
    public void onGuildJoin(GuildJoinEvent event) {
        Guild guild = event.getGuild();
        User guildOwner = guild.getOwner().getUser();

        guildOwner.openPrivateChannel().queue(privateChannel -> {
            servant.User internalGuildOwner = new servant.User(guildOwner.getIdLong());
            String p = Servant.config.getDefaultPrefix();
            User botOwner = Servant.jda.getUserById(Servant.config.getBotOwnerId());
            User bot = event.getJDA().getSelfUser();
            EmbedBuilder eb = new EmbedBuilder();

            try {
                eb.setColor(internalGuildOwner.getColor());
            } catch (SQLException e) {
                eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
            }
            eb.setAuthor(bot.getName() + " at your service!", null, guild.getIconUrl());
            eb.setDescription("Thank you for choosing me to assist you and your server.\n" +
                    "I have a lot of features. Most of them are enabled by default but some of them are not.\n" +
                    "Type `" + p + "toggle all status` to check the status of all available features.\n" +
                    "Then you can enable/disable the features to your desire.\n" +
                    "\n" +
                    "To get started, I recommend you to use my `" + p + "help` command.\n" +
                    "To get detailed help, you simply can type the command name without any arguments. E.g. `" + p + "toggle`\n" +
                    "\n" +
                    "If you need further help, you can join my support server or contact my creator directly.\n" +
                    "Support Server: [Click to join](https://" + Servant.config.getSupportGuildInv() + ")\n" +
                    "Creator Name: " + botOwner.getName() + "#" + botOwner.getDiscriminator() + "\n" +
                    "\n" +
                    "Have fun!");
            eb.setImage("https://i.imgur.com/MDRt4fA.png");
            eb.setFooter("You are receiving this message, because someone invited me to your guild (" + guild.getName() + ").", null);

            privateChannel.sendMessage(eb.build()).queue();
        });
    }

    public void onGuildLeave(GuildLeaveEvent event) {
        Guild guild = event.getGuild();
        User guildOwner = guild.getOwner().getUser();

        guildOwner.openPrivateChannel().queue(privateChannel -> {
            servant.User internalGuildOwner = new servant.User(guildOwner.getIdLong());
            User botOwner = Servant.jda.getUserById(Servant.config.getBotOwnerId());
            EmbedBuilder eb = new EmbedBuilder();

            try {
                eb.setColor(internalGuildOwner.getColor());
            } catch (SQLException e) {
                eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
            }
            eb.setAuthor("Farewell!", null, guild.getIconUrl());
            eb.setDescription("It's very sad to hear, that you don't need me anymore.\n" +
                    "If there is anything to improve, I am always open for feedback.\n" +
                    "\n" +
                    "To submit feedback, you can join my support server or contact my creator directly.\n" +
                    "Support Server: [Click to join](https://" + Servant.config.getSupportGuildInv() + ")\n" +
                    "Creator Name: " + botOwner.getName() + "#" + botOwner.getDiscriminator() + "\n");
            eb.setImage("https://i.imgur.com/MDRt4fA.png");
            eb.setFooter("You are receiving this message, because someone kicked me from your guild (" + guild.getName() + ") or your guild was deleted.", null);

            privateChannel.sendMessage(eb.build()).queue();
        });
    }
}
